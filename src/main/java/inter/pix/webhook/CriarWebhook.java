package inter.pix.webhook;

import inter.exceptions.SdkException;
import inter.model.Config;
import inter.model.CriarWebhookRequest;
import inter.model.Erro;
import inter.utils.HttpUtils;
import inter.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import static inter.constants.Constants.CERTIFICATE_EXCEPTION_MESSAGE;
import static inter.constants.Constants.ESCOPO_PIX_WEBHOOK_WRITE;
import static inter.constants.Constants.GENERIC_EXCEPTION_MESSAGE;
import static inter.constants.Constants.URL_PIX_WEBHOOK;

@Slf4j
public class CriarWebhook {
    public void criar(Config config, String webhookUrl, String chave) throws SdkException {
        log.info("CriarWebhook pix {} {} {}", config.getClientId(), webhookUrl, chave);
        String url = URL_PIX_WEBHOOK.replace("AMBIENTE", config.getAmbiente()) + "/" + chave;
        CriarWebhookRequest request = CriarWebhookRequest.builder().webhookUrl(webhookUrl).build();
        try {
            String json = JsonUtils.writePretty(request);
            HttpUtils.callPut(config, url, ESCOPO_PIX_WEBHOOK_WRITE, "Erro ao criar webhook", json);
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
