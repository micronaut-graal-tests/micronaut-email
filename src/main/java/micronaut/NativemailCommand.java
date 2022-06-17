package micronaut;

import io.micronaut.configuration.picocli.PicocliRunner;
import io.micronaut.core.annotation.TypeHint;
import io.micronaut.email.Attachment;
import io.micronaut.email.Email;
import io.micronaut.email.ses.SesEmailComposer;
import io.micronaut.http.MediaType;
import jakarta.inject.Inject;
import picocli.CommandLine.Command;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Checks that SES mail can be composed.
 *
 * In https://github.com/micronaut-projects/micronaut-email/issues/68 there was a issue composing emails with attachements.
 *
 * This is a test for it.  `./gradlew nativeRun` **should** pass.
 */
@Command(name = "nativemail", description = "...", mixinStandardHelpOptions = true)
@TypeHint(typeNames = {
        "org.apache.commons.logging.impl.LogFactoryImpl",
        "org.apache.commons.logging.LogFactory",
        "org.apache.commons.logging.impl.SimpleLog"
})
public class NativemailCommand implements Runnable {

    @Inject
    SesEmailComposer composer;

    public static void main(String[] args) {
        PicocliRunner.run(NativemailCommand.class, args);
    }

    public void run() {
        try {
            composer.compose(
                    Email.builder()
                            .to("tim@bloidonia.com")
                            .from("tim@bloidonia.com")
                            .subject("test")
                            .body("It's " + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME))
                            .attachment(
                                    Attachment.builder()
                                            .filename("attachment.pdf")
                                            .contentType(MediaType.APPLICATION_PDF)
                                            .content(NativemailCommand.class.getResourceAsStream("/hi.pdf").readAllBytes())
                                            .build()
                            )
                            .build()
            );
        } catch (IOException e) {
            throw new RuntimeException("SES mail composition failed!", e);
        }
        System.out.println("OK!");
    }
}
