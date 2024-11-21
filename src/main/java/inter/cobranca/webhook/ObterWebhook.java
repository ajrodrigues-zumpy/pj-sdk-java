package inter.cobranca.webhook;

import inter.exceptions.SdkException;
import inter.model.Config;
import inter.model.Erro;
import inter.model.Webhook;
import inter.utils.HttpUtils;
import inter.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import static inter.constants.Constants.CERTIFICATE_EXCEPTION_MESSAGE;
import static inter.constants.Constants.ESCOPO_BOLETO_COBRANCA_READ;
import static inter.constants.Constants.GENERIC_EXCEPTION_MESSAGE;
import static inter.constants.Constants.URL_BOLETOS_WEBHOOK;

@Slf4j
public class ObterWebhook {

    public Webhook obter(Config config) throws SdkException {
        log.info("ObterWebhook cobrança {}", config.getClientId());
        String url = URL_BOLETOS_WEBHOOK.replace("AMBIENTE", config.getAmbiente());
        String json = HttpUtils.callGet(config, url, ESCOPO_BOLETO_COBRANCA_READ, "Erro ao obter webhook");
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