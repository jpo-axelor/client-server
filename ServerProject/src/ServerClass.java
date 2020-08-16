import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Scanner;

public class ServerClass extends Thread {
	public static final String DEFAULT_PATH = "/home/axelor/Downloads/server/";
	public static final String QUIT = "quit";
	private ServerSocket serverSocket;

	public ServerClass(int port) throws IOException {
		serverSocket = new ServerSocket(port);
		serverSocket.setSoTimeout(60000); // 60 seconds
	}

	@Override
	public void run() {
		while (true) {
			try {
				System.out.println("Waiting for client on port " + serverSocket.getLocalPort() + "...");
				Socket socket = serverSocket.accept();
				this.serviceClient(socket);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void serviceClient(Socket socket) {
		while (true) {
			try {
				System.out.println("Just connected to " + socket.getRemoteSocketAddress());

				DataOutputStream out = new DataOutputStream(socket.getOutputStream());
				BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String input = br.readLine();
				System.out.println("Server reads client msg as : " + input);
				if (input != null && !input.isEmpty()) {
					if (input == null || !input.isEmpty() || input.equals(QUIT)) {
						break;
					}
					String[] arr = input.split(" ", 2);
					File file = new File(arr[1]);
					if (arr[0].equals("get")) {
						if (file.exists()) {
							
						}
						Scanner sc = new Scanner(file);
						String line = null;
						while (sc.hasNextLine()) {
							line = sc.nextLine();
							System.err.println(line);
							out.writeUTF(line);
						}
						sc.close();

					} else if (arr[0].equals("put")) {

					} else {
						System.err.println("Wrong imput !");
					}
				}
				socket.close();
			} catch (SocketTimeoutException s) {
				System.out.println("Socket timed out!");
				break;
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
		}
	}

	public static void main(String[] args) {
		int port = 1234;
		try {
			ServerClass serverClass = new ServerClass(port);
			serverClass.start();
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

	public static void sendFile(Socket clientSocket, String fileName) throws IOException {
		if (new File(DEFAULT_PATH + fileName).exists()) {
			System.err.println("No such file");
		}
		DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
		FileInputStream fis = new FileInputStream(DEFAULT_PATH + fileName);
		byte[] buffer = new byte[4096];
		while (fis.read(buffer) > 0) {
			out.write(buffer);
		}

		fis.close();
		out.close();
	}
}