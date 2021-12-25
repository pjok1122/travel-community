package project.board.common;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import project.board.util.Sha256Utils;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Sha256Utils.class})
public class Sha256UtilsTest {
	
	@Autowired
	Sha256Utils sha256Utils;
	
	@Test
	public void hashTest() {
		String msg = "password";
		String salt = "salt";
		String hashMsg = sha256Utils.hash(msg, salt);
		assertThat(hashMsg).isEqualTo("13601bda4ea78e55a07b98866d2be6be0744e3866f13c00c811cab608a28f322");
	}
}
