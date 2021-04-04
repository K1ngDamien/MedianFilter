import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 This part of the project is the RemoteService part. The server object is created here that registers with the RMI host.
 The Server Object Interface is also made and called here. This method retrieves the image from an URL, creates the image,
 then transforms the image into bytes so it possible to be send over the network/data communication network. When the bytes
 arrive at the client, it is unpacked, turned back into an image and used as an image in the Median Filter process.
 */

public class RemoteService extends UnicastRemoteObject implements RemoteInterface {

    private static final int PORT = 1201;
    private static final long serialVersionUID = 1L;

    private RemoteService() throws IOException {
        super();
    }

    // All different images are added in URL form, they are supposed to be commented/uncommented when wanting to be used.
    public byte[] extractBytes() throws IOException {
        URL url = new URL("https://www.upload.ee/image/13017220/image1.jpg");
//        URL url = new URL("https://www.upload.ee/image/13017224/image2.jpg");
//        URL url = new URL("https://www.upload.ee/image/13015441/image3.jpg");
//        URL url = new URL("https://www.upload.ee/image/13017225/image4.jpg");
//        URL url = new URL("https://www.upload.ee/image/13017228/image5.jpg");
        BufferedImage img = ImageIO.read(url);

        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        ImageIO.write(img, "jpg", baos );
        byte[] imageInByte=baos.toByteArray();

        return imageInByte;

    }

    public static void main(String[] args) throws IOException {
        Registry registry = LocateRegistry.createRegistry(PORT);
        registry.rebind("//localhost/MedianService", new RemoteService());
    }

}
