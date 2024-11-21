package inter.banking.webhook;

import inter.exceptions.SdkException;
import inter.model.Config;
import inter.model.Erro;
import inter.model.Webhook;
import inter.utils.HttpUtils;
import inter.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import static inter.constants.Constants.*;

@Slf4j
public class ObterWebhook {

    public Webhook obter(Config config, String tipoWebhook) throws SdkException {
        log.info("ObterWebhook banking {} {}", config.getClientId(), tipoWebhook);
        String url = URL_BANKING_WEBHOOK.replace("AMBIENTE", config.getAmbiente()) + "/" + tipoWebhook;
        String json = HttpUtils.callGet(config, url, ESCOPO_BANKING_WEBHOOK_BANKING_READ, "Erro ao obter webhook");
        try {
            return JsonUtils.read(json, Webhook.class);
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