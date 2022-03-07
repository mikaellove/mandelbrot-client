package com.company;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.InputStream;
import java.io.*;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;


public class Main
{
    public static void main(String args[]) throws Exception{

        Scanner in = new Scanner(System.in);
        System.out.println("Enter following:");
        System.out.println("height/width/maxIterations/ports");
        System.out.println("Example: 1000/1000/10/8081,8082,8083");
        String input = in.nextLine();

        String path = input.substring(0,input.lastIndexOf('/')) + "/";
        String inputtedPorts = input.substring(input.lastIndexOf('/') + 1);

        String[] ports = inputtedPorts.split(",");
        String joinedPorts = String.join(",",ports);


        InetAddress ip = InetAddress.getLocalHost();

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/mandel/" + path + joinedPorts))
                .build();
        var task = client.sendAsync(request, HttpResponse.BodyHandlers.ofByteArray())
                .thenApply(HttpResponse::body)
                .thenAccept(body -> {
                    InputStream inputStream = new ByteArrayInputStream(body);
                    try {
                        File newFile = new File("C:\\Images");
                        newFile.mkdir();
                        BufferedImage image = ImageIO.read(inputStream);
                        newFile = new File("C:\\Images\\mandelbrot.png");
                        ImageIO.write(image, "png", newFile);
                        Desktop.getDesktop().open(newFile);
                        System.out.println("Done...");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

        for(String port : ports){
            SocketClient socketClient = new SocketClient(ip,Integer.parseInt(port));
            socketClient.connectSocketToServer();
            System.out.println("Connect to localhost on port: " + port);
        }
        System.out.println("Creating image....");
        task.join();
    }
}
