import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ClientClass {
	public static final String DEFAULT_PATH = "/home/axelor/Downloads/client/";
	public static final String QUIT = "quit";

	public static void main(String[] args) throws UnknownHostException {
		InetAddress serverName = InetAddress.getLocalHost();
		int port = 1234;
		try {
			System.err.println("Connecting to " + serverName + " on port " + port);
			Socket clientSocket = new Socket(serverName, port);
			System.err.println("Just connected to " + clientSocket.getRemoteSocketAddress());

			String input = "";
			Scanner scan = new Scanner(System.in);
			input = scan.nextLine();
			System.err.println("inputMsg : " + input);

			DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
			DataInputStream in = new DataInputStream(clientSocket.getInputStream());
			while (true) {
				if (input == null || input.isEmpty() || input.equals(QUIT)) {
					break;
				}
				String[] arr = input.split(" ", 2);
				out.writeUTF(input);

				if (arr[0].equals("get")) {
					saveFile(clientSocket, arr[1],in);
				} else if (arr[0].equals("put")) {
					in.readUTF();
					sendFile(clientSocket, arr[1],out);
				} else {
					System.err.println("Wrong imput !");
				}
				while (scan.hasNextLine()) {
					input = scan.nextLine();
				}
			}
			System.err.println("Quiting....");
			scan.close();
			clientSocket.close();
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void saveFile(Socket clientSocket, String fileName,DataInputStream in) throws IOException {
//		DataInputStream in = new DataInputStream(clientSocket.getInputStream());
		FileOutputStream outStream = new FileOutputStream(DEFAULT_PATH + fileName);
		byte[] buffer = new byte[8 * 1024];
		int bytesRead;
		while ((bytesRead = in.read(buffer)) != -1) {
			outStream.write(buffer, 0, bytesRead);
		}
		outStream.close();
		in.close();
	}

	public static void sendFile(Socket clientSocket, String fileName,DataOutputStream out) throws IOException {
		if (new File(DEFAULT_PATH + fileName).exists()) {
			System.err.println("No such file");
		}
//		DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
		FileInputStream fis = new FileInputStream(DEFAULT_PATH + fileName);
		byte[] buffer = new byte[4096];
		while (fis.read(buffer) > 0) {
			out.write(buffer);
		}

		fis.close();
		out.close();
	}
}
