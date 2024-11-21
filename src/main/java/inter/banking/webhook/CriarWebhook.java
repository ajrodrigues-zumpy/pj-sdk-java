package inter.banking.webhook;

import inter.exceptions.SdkException;
import inter.model.Config;
import inter.model.CriarWebhookRequest;
import inter.model.Erro;
import inter.utils.HttpUtils;
import inter.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import static inter.constants.Constants.*;

@Slf4j
public class CriarWebhook {

    public void criar(Config config, String tipoWebhook, String webhookUrl) throws SdkException {
        log.info("CriarWebhook banking {} {} {}", config.getClientId(), tipoWebhook, webhookUrl);
        String url = URL_BANKING_WEBHOOK.replace("AMBIENTE", config.getAmbiente()) + "/" + tipoWebhook;
        CriarWebhookRequest request = CriarWebhookRequest.builder().webhookUrl(webhookUrl).build();
        try {
            String json = JsonUtils.writePretty(request);
            HttpUtils.callPut(config, url, ESCOPO_BANKING_WEBHOOK_BANKING_WRITE, "Erro ao criar webhook", json);
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