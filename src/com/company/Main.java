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

        /**
         * Running the request on a separate thread.
         * Keeping it from blocking the main thread and letting the sockets connect to the sockets on the server.
         */
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
        /**
         * Open sockets on the inputted ports.
         * I realize I'm not using the sockets quite properly, I should be transferring the image via the sockets io streams.
         * Instead of getting the image from the response body above.
          */
        for(String port : ports){
            SocketClient socketClient = new SocketClient(ip,Integer.parseInt(port));
            socketClient.connectSocketToServer();
            System.out.println("Connect to localhost on port: " + port);
        }
        System.out.println("Creating image....");

        /**
         * Wait for the request task to complete before finishing the program.
         */
        task.join();



    }
}
/**
 * Ideally I wanted something like this but had a hard time transferring the image in bytes via the io streams of the sockets.
 */
/*        try (var socket = new Socket("localhost", 8080)) {

            try (var wtr = new PrintWriter(socket.getOutputStream())) {

                // create GET request with specified ports
                wtr.print("GET /mandel/10000/10000/20/8081,8082,8083 HTTP/1.1\r\n");
                wtr.print("Host: localhost:8080");
                wtr.print("\r\n");
                wtr.flush();
                socket.shutdownOutput();

                // Open sockets on server
                for(String port : ports){
                    SocketClient socketClient = new SocketClient(ip,Integer.parseInt(port));
                    socketClient.connectSocketToServer();
                    System.out.println("Connect to localhost on port: " + port);
                }

                Then reading the input stream of the opened sockets to receive the image in bytes and convert it and save to a file.
            }
        }*/
