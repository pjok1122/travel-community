package project.board.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;

import project.board.domain.dto.GpsDecimal;

@Controller
public class MapController {
	
	private String match = "[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]";
	
	@GetMapping("/map")
	public String printMap(Model model) {
		
		String [] imageNames = {
				"pjok112231343a34363a3035e31ad48a4c8f427eb8a4b6028eb64a42.jpg",
				"pjok112231343a34363a30357cb945887b5948fdb534a480704b4dfb.jpg",
				"pjok112231343a34363a30359d951a76e6584f74aeab7c59c6f1862a.jpg",
				"pjok112231343a34363a3035475c28dc4c424603b3d9ddb20c956e5d.jpg",
				"pjok112231343a34363a303574217f527425495d9dceb0c3bf914932.jpg"
				};
		
		String baseDir = "C:/travel/community/images/2020/04/25/";
		ArrayList<GpsDecimal> imagesInfo = new ArrayList<GpsDecimal>();
		for(String imageName : imageNames) {
			File file = new File(baseDir + imageName);
			try {
				Metadata metadata = ImageMetadataReader.readMetadata(file);
				imagesInfo.add(toGpsDecimalAndTime(metadata));
			} catch (ImageProcessingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		model.addAttribute("imagesInfo", imagesInfo);
		return "map";
	}
	
	public GpsDecimal toGpsDecimalAndTime(Metadata metadata) {
		//GPS 정보 추출
		GpsDirectory gps = metadata.getFirstDirectoryOfType(GpsDirectory.class);
		double latitude = GpsToDecimal(gps.getDescription(GpsDirectory.TAG_LATITUDE_REF), gps.getDescription(GpsDirectory.TAG_LATITUDE));
		double longitude = GpsToDecimal(gps.getDescription(GpsDirectory.TAG_LONGITUDE_REF), gps.getDescription(GpsDirectory.TAG_LONGITUDE));
		
		return new GpsDecimal(latitude, longitude);
	}

	private double GpsToDecimal(String direction, String gpsInfo) {
		double decimal = 0;
		
		String[] dms = gpsInfo.replaceAll(match, "").split(" ");
		decimal = Double.parseDouble(dms[0]) + Double.parseDouble(dms[1])/60 + Double.parseDouble(dms[2])/3600;
		
		if(direction.equals("N") || direction.equals("E")) {
			return decimal;
		}
		else {
			return -decimal;
		}
		
	}
}
