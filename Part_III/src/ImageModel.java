import java.awt.image.BufferedImage;

class ImageModel {

    private volatile BufferedImage[] images = new BufferedImage[8];

    // Getter
    public BufferedImage[] getImages() {
        return images;
    }

    // Setter
    public void setImages(int index, BufferedImage newImagePart) {
        this.images[index] = newImagePart;
    }
}
