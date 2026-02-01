package com.skydiveforecast;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ApplicationTest {

    @Test
    void application_shouldBeInstantiated() {
        Application application = new Application();
        assertThat(application).isNotNull();
    }

    @Test
    void main_shouldStartApplication() {
        try (org.mockito.MockedStatic<org.springframework.boot.SpringApplication> utilities = org.mockito.Mockito
                .mockStatic(org.springframework.boot.SpringApplication.class)) {
            utilities.when(() -> org.springframework.boot.SpringApplication.run(Application.class, new String[] {}))
                    .thenReturn(null);

            Application.main(new String[] {});

            utilities.verify(() -> org.springframework.boot.SpringApplication.run(Application.class, new String[] {}));
        }
    }
}
