package inter.pix.webhook;

import inter.exceptions.SdkException;
import inter.model.Config;
import inter.model.Erro;
import inter.pix.model.FiltroBuscarCallbacks;
import inter.pix.model.PaginaCallbacks;
import inter.pix.model.RespostaBuscarCallbacks;
import inter.utils.HttpUtils;
import inter.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static inter.constants.Constants.CERTIFICATE_EXCEPTION_MESSAGE;
import static inter.constants.Constants.ESCOPO_PIX_WEBHOOK_READ;
import static inter.constants.Constants.GENERIC_EXCEPTION_MESSAGE;
import static inter.constants.Constants.URL_PIX_WEBHOOK_CALLBACKS;

@Slf4j
public class ConsultarCallbacks {

    public PaginaCallbacks buscar(Config config, String dataHoraInicio, String dataHoraFim, int pagina, Integer tamanhoPagina, FiltroBuscarCallbacks filtro) throws SdkException {
        log.info("ConsultarCallbacks {} {}-{}", config.getClientId(), dataHoraInicio, dataHoraFim);
        return getPage(config, dataHoraInicio, dataHoraFim, pagina, tamanhoPagina, filtro);
    }

    public List<RespostaBuscarCallbacks> buscar(Config config, String dataInicial, String dataFinal, FiltroBuscarCallbacks filtro) throws SdkException {
        log.info("ConsultarCallbacks {} {}-{}", config.getClientId(), dataInicial, dataFinal);
        int pagina = 0;
        PaginaCallbacks paginaCallbacks;
        List<RespostaBuscarCallbacks> callbacks = new ArrayList<>();
        do {
            paginaCallbacks = getPage(config, dataInicial, dataFinal, pagina, null, filtro);
            callbacks.addAll(paginaCallbacks.getData());
            pagina++;
        } while (pagina < paginaCallbacks.getTotalPaginas());

        return callbacks;
    }

    private PaginaCallbacks getPage(Config config, String dataHoraInicio, String dataHoraFim, int pagina, Integer tamanhoPagina, FiltroBuscarCallbacks filtro) throws SdkException {
        String url = URL_PIX_WEBHOOK_CALLBACKS.replace("AMBIENTE", config.getAmbiente())
                + "?dataHoraInicio=" + dataHoraInicio
                + "&dataHoraFim=" + dataHoraFim
                + "&pagina=" + pagina
                + (tamanhoPagina != null ? "&tamanhoPagina=" + tamanhoPagina : "")
                + addfilters(filtro);
        String json = HttpUtils.callGet(config, url, ESCOPO_PIX_WEBHOOK_READ, "Erro ao recuperar callbacks");
        try {
            return JsonUtils.read(json, PaginaCallbacks.class);
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

    private String addfilters(FiltroBuscarCallbacks filtro) {
        if (filtro == null) {
            return "";
        }

        StringBuilder filter = new StringBuilder();

        if (filtro.getTxid() != null) {
            filter.append("&txid").append("=").append(filtro.getTxid());
        }

        return filter.toString();
    }


}
