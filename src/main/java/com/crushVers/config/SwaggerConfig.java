package com.crushVers.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CrushVerse API")
                        .description("API для приложения CrushVerse")
                        .version("1.0")
                );
    }

    @Bean
    public OpenApiCustomizer openApiCustomizer() {
        return openApi -> {
            try {
                ObjectMapper mapper = new ObjectMapper();
                InputStream inputStream = getClass().getClassLoader().getResourceAsStream("swagger-descriptions.json");
                if (inputStream == null) {
                    System.out.println("⚠️ Файл swagger-descriptions.json не найден");
                    return;
                }
                Map<String, Object> descriptions = mapper.readValue(inputStream, Map.class);
                Map<String, Object> authDescriptions = (Map<String, Object>) descriptions.get("auth");
                if (openApi.getPaths() != null) {
                    openApi.getPaths().forEach((path, pathItem) -> {
                        String operationId = getOperationId(path);
                        if (operationId != null) {
                            if (pathItem.getPost() != null) {
                                applyOperation(pathItem.getPost(), authDescriptions, operationId);
                            }
                            if (pathItem.getGet() != null) {
                                applyOperation(pathItem.getGet(), authDescriptions, operationId);
                            }
                        }
                    });
                }
            } catch (Exception e) {
                System.err.println("❌ Ошибка загрузки Swagger описаний: " + e.getMessage());
            }
        };
    }

    private String getOperationId(String path) {
        String[] parts = path.split("/");
        for (String part : parts) {
            if (!part.isEmpty() && !part.equals("api") && !part.startsWith("{")) {
                return part.replace("-", "");
            }
        }
        return null;
    }

    private void applyOperation(Operation operation, Map<String, Object> descriptions, String operationId) {
        Map<String, Object> operationDesc = (Map<String, Object>) descriptions.get(operationId);

        if (operationDesc == null) return;


        if (operationDesc.containsKey("summary")) {
            operation.setSummary((String) operationDesc.get("summary"));
        }


        if (operationDesc.containsKey("description")) {
            operation.setDescription((String) operationDesc.get("description"));
        }


        if (operationDesc.containsKey("parameters")) {
            List<Map<String, Object>> parametersList =
                    (List<Map<String, Object>>) operationDesc.get("parameters");

            List<Parameter> parameters = new ArrayList<>();

            for (Map<String, Object> paramData : parametersList) {
                Parameter parameter = new Parameter();

                if (paramData.containsKey("name")) {
                    parameter.setName((String) paramData.get("name"));
                }
                if (paramData.containsKey("in")) {
                    parameter.setIn((String) paramData.get("in"));
                }
                if (paramData.containsKey("description")) {
                    parameter.setDescription((String) paramData.get("description"));
                }
                if (paramData.containsKey("required")) {
                    parameter.setRequired((Boolean) paramData.get("required"));
                }

                // Обработка schema для параметра
                if (paramData.containsKey("schema")) {
                    Map<String, Object> schemaData = (Map<String, Object>) paramData.get("schema");
                    Schema schema = new Schema();

                    if (schemaData.containsKey("type")) {
                        schema.setType((String) schemaData.get("type"));
                    }
                    if (schemaData.containsKey("example")) {
                        schema.setExample(schemaData.get("example"));
                    }
                    if (schemaData.containsKey("enum")) {
                        schema.setEnum((List<String>) schemaData.get("enum"));
                    }

                    parameter.setSchema(schema);
                }

                parameters.add(parameter);
            }

            operation.setParameters(parameters);
        }


        if (operationDesc.containsKey("requestBody")) {
            Map<String, Object> requestBodyData = (Map<String, Object>) operationDesc.get("requestBody");
            io.swagger.v3.oas.models.parameters.RequestBody requestBody = new io.swagger.v3.oas.models.parameters.RequestBody();

            if (requestBodyData.containsKey("description")) {
                requestBody.setDescription((String) requestBodyData.get("description"));
            }
            if (requestBodyData.containsKey("required")) {
                requestBody.setRequired((Boolean) requestBodyData.get("required"));
            }
            if (requestBodyData.containsKey("content")) {
                Map<String, Map<String, Object>> contentMap =
                        (Map<String, Map<String, Object>>) requestBodyData.get("content");
                Content content = new Content();

                for (Map.Entry<String, Map<String, Object>> entry : contentMap.entrySet()) {
                    String mediaType = entry.getKey();
                    Map<String, Object> mediaData = entry.getValue();

                    MediaType mt = new MediaType();

                    // Обработка schema
                    if (mediaData.containsKey("schema")) {
                        Map<String, Object> schemaMap = (Map<String, Object>) mediaData.get("schema");
                        Schema schema = new Schema();

                        if (schemaMap.containsKey("type")) {
                            schema.setType((String) schemaMap.get("type"));
                        }

                        if (schemaMap.containsKey("properties")) {
                            Map<String, Map<String, Object>> properties =
                                    (Map<String, Map<String, Object>>) schemaMap.get("properties");

                            for (Map.Entry<String, Map<String, Object>> propEntry : properties.entrySet()) {
                                String propName = propEntry.getKey();
                                Map<String, Object> propData = propEntry.getValue();

                                Schema propSchema = new Schema();

                                if (propData.containsKey("type")) {
                                    propSchema.setType((String) propData.get("type"));
                                }
                                if (propData.containsKey("description")) {
                                    propSchema.setDescription((String) propData.get("description"));
                                }
                                if (propData.containsKey("example")) {
                                    propSchema.setExample(propData.get("example"));
                                }
                                if (propData.containsKey("minLength")) {
                                    propSchema.setMinLength((Integer) propData.get("minLength"));
                                }

                                schema.addProperty(propName, propSchema);
                            }
                        }

                        if (schemaMap.containsKey("required")) {
                            schema.setRequired((List<String>) schemaMap.get("required"));
                        }

                        mt.setSchema(schema);
                    }

                    // Обработка примеров
                    if (mediaData.containsKey("examples")) {
                        Map<String, Map<String, Object>> examples =
                                (Map<String, Map<String, Object>>) mediaData.get("examples");
                        for (Map.Entry<String, Map<String, Object>> exEntry : examples.entrySet()) {
                            io.swagger.v3.oas.models.examples.Example example = new io.swagger.v3.oas.models.examples.Example();
                            Map<String, Object> exampleData = exEntry.getValue();
                            if (exampleData.containsKey("summary")) {
                                example.setSummary((String) exampleData.get("summary"));
                            }
                            if (exampleData.containsKey("value")) {
                                example.setValue(exampleData.get("value"));
                            }
                            mt.addExamples(exEntry.getKey(), example);
                        }
                    }
                    content.addMediaType(mediaType, mt);
                }
                requestBody.setContent(content);
            }

            operation.setRequestBody(requestBody);
        }


        if (operationDesc.containsKey("responses")) {
            Map<String, Map<String, Object>> responseMap =
                    (Map<String, Map<String, Object>>) operationDesc.get("responses");

            ApiResponses responses = new ApiResponses();

            for (Map.Entry<String, Map<String, Object>> entry : responseMap.entrySet()) {
                String code = entry.getKey();
                Map<String, Object> responseData = entry.getValue();

                ApiResponse response = new ApiResponse()
                        .description((String) responseData.get("description"));

                if (responseData.containsKey("content")) {
                    Map<String, Map<String, Object>> contentMap =
                            (Map<String, Map<String, Object>>) responseData.get("content");

                    Content content = new Content();
                    for (Map.Entry<String, Map<String, Object>> contentEntry : contentMap.entrySet()) {
                        String mediaType = contentEntry.getKey();
                        Map<String, Object> mediaData = contentEntry.getValue();

                        MediaType mt = new MediaType();
                        if (mediaData.containsKey("examples")) {
                            Map<String, Map<String, Object>> examples = (Map<String, Map<String, Object>>) mediaData.get("examples");
                            for (Map.Entry<String, Map<String, Object>> exEntry : examples.entrySet()) {
                                Example example = new Example();
                                Map<String, Object> exampleData = exEntry.getValue();
                                if (exampleData.containsKey("summary")) {
                                    example.setSummary((String) exampleData.get("summary"));
                                }
                                if (exampleData.containsKey("value")) {
                                    example.setValue(exampleData.get("value"));
                                }
                                mt.addExamples(exEntry.getKey(), example);
                            }
                        }
                        content.addMediaType(mediaType, mt);
                    }
                    response.setContent(content);
                }

                responses.addApiResponse(code, response);
            }

            operation.setResponses(responses);
        }
    }
}