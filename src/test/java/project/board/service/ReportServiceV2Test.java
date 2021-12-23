package project.board.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import project.board.enums.ReportTargetType;

import static org.assertj.core.api.Assertions.*;

class ReportServiceV2Test {

    @Test
    void valueOf_PASS() {
        String type = "COMMENT";
        ReportTargetType result = ReportTargetType.valueOf(type);

        assertThat(result).isEqualTo(ReportTargetType.COMMENT);
    }

    @Test
    void valueOf_Illegal() {
        String type = "comment";
        assertThatThrownBy(() -> ReportTargetType.valueOf(type)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void valueOf_Illegal2() {
        String type = "COMMENT2";

        assertThatThrownBy(() -> ReportTargetType.valueOf(type)).isInstanceOf(IllegalArgumentException.class);
    }

}