package com.example.dddeck;
import java.io.IOException;

import com.jcraft.jsch.*;

import java.io.InputStream;
import java.nio.file.*;
import java.util.Vector;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class SSHManager {

    public String sshExec(String host, String user, String password, String port, String command){
        System.out.println(App.timestamp() + "SSHManager sshStatus()");
        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(user, host, 22); //todo: add port
            session.setPassword(password);

            System.out.println(App.timestamp() + "\nUser: " + user + "\nPassword: " + password + "\nHost: " + host + "\nPort: " + port);

            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            ChannelExec channelExec = (ChannelExec)
                session.openChannel("exec");
            channelExec.setCommand(command);

            InputStream in = channelExec.getInputStream();
            channelExec.connect();

            // Use StringBuilder to accumulate the output
            StringBuilder outputBuffer = new StringBuilder();
            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    outputBuffer.append(new String(tmp, 0, i));
                }
                if (channelExec.isClosed()) {
                    if (in.available() > 0) continue;
                    // System.out.println("Exit status: " + channelExec.getExitStatus());
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                    ee.printStackTrace();
                    return null;
                }
            }
            
            channelExec.disconnect();
            session.disconnect();

            String commandOutput = outputBuffer.toString();

            // Now you can use commandOutput as needed
            // System.out.println("SSH Command Output: ");
            System.out.println(App.timestamp() + "[SteamDeck] " + commandOutput);
            return commandOutput;

            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void getRemoteDir(String remoteDir, String localDir, String host, String user, String password){
        JSch jsch = new JSch();
        Session session = null;
        ChannelSftp channelSftp = null;

        try{
            session = jsch.getSession(user, host, 22);
            session.setPassword(password);

            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();

            downloadFolder(channelSftp, remoteDir, localDir);
        }   catch (Exception e){
            e.printStackTrace();
        } finally {
            if (channelSftp != null && channelSftp.isConnected()){
                channelSftp.disconnect();
            }
            if (session != null && session.isConnected()){
                session.disconnect();
            }
        }
    }

        private static void downloadFolder(ChannelSftp channelSftp, String remoteDir, String localDir) throws SftpException, IOException {
        Vector<ChannelSftp.LsEntry> fileAndFolderList = channelSftp.ls(remoteDir); // List source directory structure.

        for (ChannelSftp.LsEntry item : fileAndFolderList) { // Iterate objects in the list to get file/folder names.
            String fileName = item.getFilename();

            // Skip ".", ".." entries
            if (fileName.equals(".") || fileName.equals("..")) {
                continue;
            }

            String remoteFilePath = remoteDir + "/" + fileName;
            String localFilePath = localDir + File.separator + fileName;

            if (item.getAttrs().isDir()) { // If it is a directory, recursively copy it
                new File(localFilePath).mkdirs(); // Create the local directory.
                downloadFolder(channelSftp, remoteFilePath, localFilePath); // Recursively download subdirectories.
            } else { // If it is a file, download it.
                try (FileOutputStream fos = new FileOutputStream(new File(localFilePath))) {
                    channelSftp.get(remoteFilePath, fos);
                }
            }
        }
    }

}

