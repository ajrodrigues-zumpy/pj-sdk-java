package inter.banking.pagamento;

import com.fasterxml.jackson.core.type.TypeReference;
import inter.banking.model.FiltroBuscarPagamentos;
import inter.banking.model.Pagamento;
import inter.exceptions.SdkException;
import inter.model.Config;
import inter.model.Erro;
import inter.utils.HttpUtils;
import inter.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

import static inter.constants.Constants.CERTIFICATE_EXCEPTION_MESSAGE;
import static inter.constants.Constants.ESCOPO_PAGAMENTO_BOLETO_READ;
import static inter.constants.Constants.GENERIC_EXCEPTION_MESSAGE;
import static inter.constants.Constants.URL_BANKING_PAGAMENTO;

@Slf4j
public class BuscarPagamentos {

    public List<Pagamento> buscar(Config config, String dataInicial, String dataFinal, FiltroBuscarPagamentos filtro) throws SdkException {
        log.info("BuscarPagamentos {} {}-{}", config.getClientId(), dataInicial, dataFinal);
        String url = URL_BANKING_PAGAMENTO.replace("AMBIENTE", config.getAmbiente()) + "?dataInicio=" + dataInicial + "&dataFim=" + dataFinal
                + addfilters(filtro);
        String json = HttpUtils.callGet(config, url, ESCOPO_PAGAMENTO_BOLETO_READ, "Erro ao buscar pagamentos");
        try {
            return JsonUtils.read(json, new TypeReference<List<Pagamento>>() {
            });
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

    private String addfilters(FiltroBuscarPagamentos filtro) {
        if (filtro == null) {
            return "";
        }
        StringBuilder filter = new StringBuilder();
        if (filtro.getCodBarraLinhaDigitavel() != null) {
            filter.append("&codBarraLinhaDigitavel").append("=").append(filtro.getCodBarraLinhaDigitavel());
        }
        if (filtro.getCodigoTransacao() != null) {
            filter.append("&codigoTransacao").append("=").append(filtro.getCodigoTransacao());
        }
        if (filtro.getFiltrarDataPor() != null) {
            filter.append("&filtrarDataPor").append("=").append(filtro.getFiltrarDataPor().toString());
        }
        return filter.toString();
    }

}