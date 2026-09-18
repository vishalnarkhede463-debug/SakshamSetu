package com.govskill;

import com.govskill.controllers.ApiController;
import com.govskill.dao.DBConnection;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.util.concurrent.Executors;

public class Main {

    private static final int DEFAULT_PORT = 8080;

    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        String envPort = System.getenv("PORT");
        if (envPort != null) {
            try {
                port = Integer.parseInt(envPort);
            } catch (NumberFormatException ignored) {}
        }

        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            server.setExecutor(Executors.newFixedThreadPool(16));

            ApiController apiController = new ApiController();

            // API handler
            server.createContext("/api", apiController::handle);

            // Static file handler for frontend
            server.createContext("/", new StaticFileHandler());

            server.start();

            System.out.println("==================================================================");
            System.out.println("  GovSkill Connect - Government Skill & Employment Portal Server  ");
            System.out.println("==================================================================");
            System.out.println("  Status: RUNNING");
            System.out.println("  Portal URL:   http://localhost:" + port + "/");
            System.out.println("  Home Page:    http://localhost:" + port + "/index.html");
            System.out.println("  Database:     " + (DBConnection.isMySqlAvailable() ? "MySQL Active" : "In-Memory Relational Engine (Seeded)"));
            System.out.println("==================================================================");
            System.out.println("  Demo Credentials:");
            System.out.println("  - Student:    rahul.sharma@gmail.com  / student123");
            System.out.println("  - Employer:   hr@isro.gov.in          / employer123");
            System.out.println("  - Admin:      admin@govskill.gov.in   / admin123");
            System.out.println("==================================================================");

        } catch (IOException e) {
            System.err.println("Fatal error starting server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static class StaticFileHandler implements HttpHandler {

        private final String[] searchPaths = {
            "frontend", "../frontend", "GovSkillConnect/frontend",
            "frontend/images", "../frontend/images", "GovSkillConnect/frontend/images",
            "."
        };

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if ("/".equals(path) || path.isEmpty()) {
                path = "/index.html";
            }

            File targetFile = resolveFile(path);

            if (targetFile == null || !targetFile.exists() || targetFile.isDirectory()) {
                // If requesting an html page without .html extension
                if (!path.contains(".")) {
                    File htmlFile = resolveFile(path + ".html");
                    if (htmlFile != null && htmlFile.exists()) {
                        targetFile = htmlFile;
                    }
                }
            }

            if (targetFile == null || !targetFile.exists() || targetFile.isDirectory()) {
                String notFound = "<!DOCTYPE html><html><head><title>404 Not Found</title></head><body><h1>404 Not Found</h1><p>Resource " + path + " not found.</p><a href='/index.html'>Go to Home</a></body></html>";
                byte[] bytes = notFound.getBytes();
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                exchange.sendResponseHeaders(404, bytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(bytes);
                }
                return;
            }

            String mime = getMimeType(targetFile.getName());
            exchange.getResponseHeaders().set("Content-Type", mime);
            exchange.sendResponseHeaders(200, targetFile.length());

            try (FileInputStream fis = new FileInputStream(targetFile);
                 OutputStream os = exchange.getResponseBody()) {
                byte[] buffer = new byte[8192];
                int count;
                while ((count = fis.read(buffer)) > 0) {
                    os.write(buffer, 0, count);
                }
            }
        }

        private File resolveFile(String relativePath) {
            String clean = relativePath.startsWith("/") ? relativePath.substring(1) : relativePath;
            for (String base : searchPaths) {
                File candidate = new File(base, clean);
                if (candidate.exists() && candidate.isFile()) {
                    return candidate;
                }
            }
            // Also check uploads directory directly
            if (clean.startsWith("uploads/")) {
                File uploadCandidate = new File(clean);
                if (uploadCandidate.exists() && uploadCandidate.isFile()) return uploadCandidate;
            }
            return null;
        }

        private String getMimeType(String filename) {
            String f = filename.toLowerCase();
            if (f.endsWith(".html") || f.endsWith(".htm")) return "text/html; charset=UTF-8";
            if (f.endsWith(".css")) return "text/css; charset=UTF-8";
            if (f.endsWith(".js")) return "application/javascript; charset=UTF-8";
            if (f.endsWith(".json")) return "application/json; charset=UTF-8";
            if (f.endsWith(".png")) return "image/png";
            if (f.endsWith(".jpg") || f.endsWith(".jpeg")) return "image/jpeg";
            if (f.endsWith(".gif")) return "image/gif";
            if (f.endsWith(".svg")) return "image/svg+xml";
            if (f.endsWith(".pdf")) return "application/pdf";
            if (f.endsWith(".ico")) return "image/x-icon";
            return "application/octet-stream";
        }
    }
}
