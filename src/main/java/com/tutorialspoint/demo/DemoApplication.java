package com.tutorialspoint.demo;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
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
    public ImageContent fileUpload(@RequestParam("file") MultipartFile file) {
        BufferedImage img;
        try {
            /*
                File imageFile = new File(file.getOriginalFilename());
                imageFile.createNewFile();




                FileOutputStream fout = new FileOutputStream(imageFile);
                fout.write(file.getBytes());
                fout.close();

             */

            img = ImageIO.read(file.getInputStream());

        } catch (IOException e) {
            ImageContent tmp = new ImageContent();
            tmp.setText(e.getMessage());
            return tmp;
        }


        String text = executeTesseract(img);


        ImageContent content = new ImageContent();
        content.setText(text);
        return content;
    }

    private String executeTesseract(BufferedImage imageFile) {
        Tesseract instance = new Tesseract();

        instance.setDatapath("/usr/share/tesseract-ocr/4.00/tessdata");
        instance.setLanguage("eng");

        String result = "";

        try {
            result = instance.doOCR(imageFile);

        } catch (TesseractException e) {
            result = e.getMessage();
        }

        return result;
    }
}
