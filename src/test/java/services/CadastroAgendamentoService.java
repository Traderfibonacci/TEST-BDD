package services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import model.AgendamentoModel;
import org.bson.types.ObjectId;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import static io.restassured.RestAssured.given;

public class CadastroAgendamentoService {
    final AgendamentoModel agendamentoModel = new AgendamentoModel();
    public final Gson gson = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create();
    public Response response;
    String baseUrl = "http://localhost:8080";

    public void setFieldsDelivery(String field, String value) {
        switch (field) {
            case "nomeCliente" -> agendamentoModel.setNomeCliente(value);
            case "dataAgendamento" -> {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                agendamentoModel.setDataAgendamento(LocalDate.parse(value, formatter));
            }
            case "tipoMaterial" -> agendamentoModel.setTipoMaterial(value);
            case "descricao" -> agendamentoModel.setDescricao(value);
            default -> throw new IllegalStateException("Unexpected field: " + field);
        }
    }

    public void createDelivery(String endPoint) {
        String url = baseUrl + endPoint;
        String dataAgendamento = agendamentoModel.getDataAgendamento() != null ?
                agendamentoModel.getDataAgendamento().format(DateTimeFormatter.ISO_LOCAL_DATE) : "";
        String id = new ObjectId().toString();

        String jsonBody = String.format("{\"id\":\"%s\", \"nomeCliente\":\"%s\", \"dataAgendamento\":\"%s\", \"tipoMaterial\":\"%s\", \"descricao\":\"%s\"}",
                id,
                agendamentoModel.getNomeCliente() != null ? agendamentoModel.getNomeCliente() : "",
                dataAgendamento,
                agendamentoModel.getTipoMaterial() != null ? agendamentoModel.getTipoMaterial() : "",
                agendamentoModel.getDescricao() != null ? agendamentoModel.getDescricao() : "");

        System.out.println("JSON Enviado: " + jsonBody);
        response = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(jsonBody)
                .when()
                .post(url)
                .then()
                .extract()
                .response();

        System.out.println("Resposta Recebida: " + response.asString());
    }

    public JsonObject retrieveCadastroByName(String nomeCliente) {
        String url = baseUrl + "/api/agendamento/nome/" + nomeCliente;
        Response response = given()
                .accept(ContentType.JSON)
                .when()
                .get(url)
                .then()
                .statusCode(200)
                .extract()
                .response();

        Gson gson = new Gson();
        return gson.fromJson(response.jsonPath().prettify(), JsonObject.class);
    }

    public Response listarAgendamentosPorPeriodo(String dataInicio, String dataFinal) {
        String url = baseUrl + "/api/agendamento?dataInicio=" + dataInicio + "&dataFinal=" + dataFinal;
        Response response = given()
                .accept(ContentType.JSON)
                .when()
                .get(url)
                .then()
                .extract()
                .response();

        if (response.statusCode() != 200) {
            throw new AssertionError("Expected status code 200, but got: " + response.statusCode());
        }

        return response;
    }
    public String obterMensagemDeErroDaResposta() {
        if (response != null) {
            JsonObject jsonResponse = gson.fromJson(response.asString(), JsonObject.class);
            if (jsonResponse.has("mensagemDeErro")) {
                return jsonResponse.get("mensagemDeErro").getAsString();
            } else if (jsonResponse.has("descricao")) {
                return jsonResponse.get("descricao").getAsString();
            } else if (jsonResponse.has("nomeCliente")) {
                return jsonResponse.get("nomeCliente").getAsString();
            }
        }
        return null;
    }

    String schemasPath = "src/test/resources/schemas/";
    JSONObject jsonSchema;
    private final ObjectMapper mapper = new ObjectMapper();

    private JSONObject loadJsonFromFile(String filePath) throws IOException {
        try (InputStream inputStream = Files.newInputStream(Paths.get(filePath))) {
            JSONTokener tokener = new JSONTokener(inputStream);
            return new JSONObject(tokener);
        }
    }
    public void setContract(String contract) throws IOException {
        switch (contract) {
            case "Cadastro bem-sucedido de agendamento" -> jsonSchema = loadJsonFromFile(schemasPath + "cadastro-bem-sucedido-de-agendamento.json");
            default -> throw new IllegalStateException("Unexpected contract" + contract);
        }
    }
    public Set<ValidationMessage> validateResponseAgainstSchema() throws IOException
    {
        JSONObject jsonResponse = new JSONObject(response.getBody().asString());
        JsonSchemaFactory schemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4);
        JsonSchema schema = schemaFactory.getSchema(jsonSchema.toString());
        JsonNode jsonResponseNode = mapper.readTree(jsonResponse.toString());
        Set<ValidationMessage> schemaValidationErrors = schema.validate(jsonResponseNode);
        return schemaValidationErrors;
    }
}
