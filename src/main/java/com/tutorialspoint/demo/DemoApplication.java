package com.tutorialspoint.demo;

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

    @GetMapping(path = "/add")
    public @ResponseBody
    String addNewUser(@RequestParam String name, @RequestParam String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        userRepository.save(user);

        return "Saved";
    }

    @GetMapping(path = "/all")
    public @ResponseBody
    Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    @RequestMapping(value = "/getTheMessages", method = RequestMethod.GET)
    public List<Message> hello() {
        List<Message> list = new ArrayList<>();
        for (Message message : messageRepository.findAll()) {
            list.add(message);
        }

        return list;
    }

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public String test() {
        return "TESTING";
    }


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

    @RequestMapping(value = "/messages", method = RequestMethod.POST)
    public void createMessage(@RequestBody Message message) {
        messageRepository.save(message);
    }

    @PutMapping(value = "/delete")
    public void deleteMessages(@RequestBody List<Integer> ids) {
        for (Integer id : ids) {
            messageRepository.delete(id);
        }
    }

    @PutMapping(value = "/update")
    public void updateMessage(@RequestBody Message message) {
        messageRepository.save(message);
    }

    @PostMapping(value = "/upload")
    public String fileUpload(@RequestParam("file") MultipartFile file) throws IOException {
        String workingDirectory = "C:/Users/Florian-LucianStefan/Desktop/uploads/";

        File convertFile = new File(workingDirectory + file.getOriginalFilename());
        convertFile.createNewFile();


        FileOutputStream fout = new FileOutputStream(convertFile);
        fout.write(file.getBytes());
        fout.close();


        String command = "tesseract " + workingDirectory + file.getOriginalFilename() + " " + workingDirectory + file.getOriginalFilename();

        System.out.println(command);

        executeCommand(command);


        return "File is upload successfully";
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
