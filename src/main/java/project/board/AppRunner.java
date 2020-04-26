package project.board;

import java.io.File;
import java.util.Collection;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifDirectoryBase;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;


@Component
public class AppRunner implements ApplicationRunner{
	@Override
	public void run(ApplicationArguments args) throws Exception {
		File file = new File("C:/travel/community/images/2020/04/25/pjok112230323a30363a313379e697b37ea14ce39dbf6ed1ce03cc06.jpg");
		
		Metadata metadata = JpegMetadataReader.readMetadata(file);
		for (Directory directory : metadata.getDirectoriesOfType(GpsDirectory.class)) {
			System.out.println(directory.getClass());
            for (Tag tag : directory.getTags()){
            	if(tag.getTagName().equals("User Comment"))continue;
                System.out.print("[");
                System.out.print(tag.getDirectoryName());
                System.out.print("]");
                System.out.print(tag.getTagName());
                System.out.print(" - ");
                System.out.println(tag.getDescription());
            }
		}
        for (Directory directory : metadata.getDirectoriesOfType(ExifSubIFDDirectory.class)) {
        	System.out.println(directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL));
        	System.out.println(directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_DIGITIZED));
        }
	}
}
