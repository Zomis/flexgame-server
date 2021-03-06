package net.zomis.spring.games;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.contains;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import net.zomis.monopoly.model.MonopolyConfig;
import net.zomis.spring.games.messages.CreateGameRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GamesControllerMonopolyBasicTest {

    @Autowired
    private TestRestTemplate restTemplate;

	@Autowired
	private WebApplicationContext ctx;

    @Value("${local.server.port}")
    private int port;
	private MockMvc mockMvc;

	@Before
	public void setUp() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx).build();
	}

    @Test
    public void gameListTypes() throws Exception {
        this.mockMvc.perform(get("/games"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", contains("monopoly")));
    }

    @Test
	public void gameListRequestShouldReturnList() throws Exception {
        this.mockMvc.perform(get("/games/monopoly"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.games").isArray());
	}

	@Test
	public void createGameRequestShouldCreateGame() throws Exception {
        CreateGameRequest request = new CreateGameRequest();
        request.setPlayerName("Zomis");
        MonopolyConfig config = new MonopolyConfig();
        config.speedDie = true;
        request.setGameConfig(config);

		this.mockMvc.perform(postRequest("/games/monopoly", request))
            .andDo(print())
            .andExpect(status().is(201))
            .andExpect(jsonPath("$.gameId").isString());
	}

    private RequestBuilder postRequest(String url, Object request) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(request);
        return post(url).contentType(MediaType.APPLICATION_JSON_UTF8)
            .content(requestJson);
    }

}