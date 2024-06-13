package inter.cobrancav3.webhook;

import com.fasterxml.jackson.databind.ObjectMapper;
import inter.exceptions.SdkException;
import inter.model.Config;
import inter.model.CriarWebhookRequest;
import inter.model.Erro;
import inter.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import static inter.constants.Constants.CERTIFICATE_EXCEPTION_MESSAGE;
import static inter.constants.Constants.ESCOPO_BOLETO_COBRANCA_WRITE;
import static inter.constants.Constants.GENERIC_EXCEPTION_MESSAGE;
import static inter.constants.Constants.URL_COBRANCAS_WEBHOOK;

@Slf4j
public class CriaWebhook {

    public void criar(Config config, String webhookUrl) throws SdkException {
        log.info("CriarWebhook cobrançaV3 {} {}", config.getClientId(), webhookUrl);
        String url = URL_COBRANCAS_WEBHOOK.replace("AMBIENTE", config.getAmbiente());
        CriarWebhookRequest request = CriarWebhookRequest.builder().webhookUrl(webhookUrl).build();
        try {
            String json = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(request);
            HttpUtils.callPut(config, url, ESCOPO_BOLETO_COBRANCA_WRITE, "Erro ao criar webhook", json);
        } catch (IOException ioException) {
            log.error(GENERIC_EXCEPTION_MESSAGE, ioException);
            throw new SdkException(
                    ioException.getMessage(),
                    Erro.builder()
                            .title(CERTIFICATE_EXCEPTION_MESSAGE).
                            detail(ioException.getMessage())
                            .build()
            );
        }
    }

}