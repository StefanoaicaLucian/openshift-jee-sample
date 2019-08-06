package com.tutorialspoint.demo;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@SpringBootApplication
@RestController
public class DemoApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(DemoApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageRepository messageRepository;

    @CrossOrigin
    @GetMapping(path = "/add")
    public @ResponseBody
    String addNewUser(@RequestParam String name, @RequestParam String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        userRepository.save(user);

        return "Saved";
    }

    @CrossOrigin
    @GetMapping(path = "/all")
    public @ResponseBody
    Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    @CrossOrigin
    @RequestMapping(value = "/getTheMessages", method = RequestMethod.GET)
    public List<Message> hello() {
        List<Message> list = new ArrayList<>();
        for (Message message : messageRepository.findAll()) {
            list.add(message);
        }

        return list;
    }

    @CrossOrigin
    @RequestMapping(value = "/fetchData", method = RequestMethod.POST)
    public PageResponse fetchData(@RequestBody PageRequest request) {
        Page<Message> page = messageRepository.findAll(new org.springframework.data.domain.PageRequest(
                request.getPageIndex(), request.getPageSize()));

        Iterator<Message> iterator = page.iterator();

        List<Message> messages = new ArrayList<>();

        while (iterator.hasNext()) {
            messages.add(iterator.next());
        }

        PageResponse response = new PageResponse();
        response.setMessages(messages);
        response.setTotalRecordsCount(messageRepository.count());

        return response;
    }

    @CrossOrigin
    @RequestMapping(value = "/messages", method = RequestMethod.POST)
    public void createMessage(@RequestBody Message message) {
        messageRepository.save(message);
    }

    @CrossOrigin
    @PutMapping(value = "/delete")
    public void deleteMessages(@RequestBody List<Integer> ids) {
        for (Integer id : ids) {
            messageRepository.delete(id);
        }
    }

    @CrossOrigin
    @PutMapping(value = "/update")
    public void updateMessage(@RequestBody Message message) {
        messageRepository.save(message);
    }

    @CrossOrigin
    @PostMapping(value = "/upload")
    public ImageContent fileUpload(@RequestParam("file") MultipartFile file) throws IOException {
        File imageFile = new File(file.getOriginalFilename());
        imageFile.createNewFile();


        FileOutputStream fout = new FileOutputStream(imageFile);
        fout.write(file.getBytes());
        fout.close();

        String imageFilePath = imageFile.getAbsolutePath();

        String textFilePath = imageFilePath.substring(0, imageFilePath.indexOf("."));

        String command = "tesseract " + imageFilePath + " " + textFilePath;

        System.out.println(command);
        executeCommand(command);
        System.out.println("Done!");

        File textFile = new File(textFilePath + ".txt");

        List<String> lines = FileUtils.readLines(textFile, "UTF-8");

        StringBuilder buffer = new StringBuilder();
        for (String line : lines) {
            buffer.append(line);
        }

        imageFile.delete();
        textFile.delete();

        ImageContent content = new ImageContent();
        content.setText(buffer.toString());
        return content;
    }

    private String executeCommand(String command) {
        StringBuilder output = new StringBuilder();

        Process process;
        try {
            process = Runtime.getRuntime().exec(command);
            process.waitFor();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));


            String line;

            while ((line = reader.readLine()) != null) {
                output.append(line);
                output.append("\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return output.toString();
    }
}
