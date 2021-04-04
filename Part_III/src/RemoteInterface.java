import java.io.IOException;
import java.rmi.Remote;

public interface RemoteInterface extends Remote {

    byte[] extractBytes() throws IOException;

}
