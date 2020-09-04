package br.org.rpf.cagef.controller;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hibernate.ObjectNotFoundException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.org.rpf.cagef.controller.exception.ResourceExceptionHandler;
import br.org.rpf.cagef.dto.http.request.city.UserRequestParamsDTO;
import br.org.rpf.cagef.dto.user.UserDTO;
import br.org.rpf.cagef.entity.User;
import br.org.rpf.cagef.service.UserService;
import br.org.rpf.cagef.service.impl.UserServiceImplTest;

@RunWith(SpringRunner.class)
public class UserControllerTest {

	private static final String URL = "/users";
	private static final String BY_ID = URL.concat("/1");
	private MockMvc mockMvc;

	@Rule
	public ExpectedException expectedEx = ExpectedException.none();

	@MockBean
	private UserService userService;
	
	@InjectMocks
	private UserController userController;
	
	@Before
    public void setUp() {
            this.mockMvc = MockMvcBuilders
            .standaloneSetup(userController)
            .setControllerAdvice(new ResourceExceptionHandler())
            .build();
            MockitoAnnotations.initMocks(this);
    }
	
	@Test
    public void findAllBadRequestError() throws Exception {
        when(this.userService.findAll(any(UserRequestParamsDTO.class))).thenThrow(new DataIntegrityViolationException("Error"));
        this.mockMvc.perform(get(URL).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("timestamp", notNullValue()))
        .andExpect(jsonPath("status", is(400)))
        .andExpect(jsonPath("message", is("Não é possível remover este registro pois está sendo utilizado")))
        .andExpect(jsonPath("error", is("Error")))
        .andExpect(jsonPath("path", is(URL)));
    }
	
	@Test
    public void findAllSuccessTest() throws Exception {
		when(this.userService.findAll(any(UserRequestParamsDTO.class)))
				.thenReturn(new PageImpl<>(UserServiceImplTest.getUsersList()));
		this.mockMvc.perform(get(URL).contentType(MediaType.APPLICATION_JSON))
	    .andExpect(status().isOk())
	    .andExpect(jsonPath("$.content", hasSize(4)))
	    .andExpect(jsonPath("$.content.[0].id", is(1)))
	    .andExpect(jsonPath("$.content.[0].name", is("Casa Branca")))
	    .andExpect(jsonPath("$.content.[0].city.id", is(1)))
        .andExpect(jsonPath("$.content.[0].city.name", is("Teste")))
        .andExpect(jsonPath("$.content.[0].city.state", is("SP")))
        .andExpect(jsonPath("$.content.[0].city.regional", is(true)))
        .andExpect(jsonPath("$.content.[0].email", is("mail@mail.com")))
        .andExpect(jsonPath("$.content.[0].role", is("ROLE_USUARIO")))
        .andExpect(jsonPath("$.content.[1].id", is(2)))
	    .andExpect(jsonPath("$.content.[1].name", is("Mococa")))
	    .andExpect(jsonPath("$.content.[1].city.id", is(1)))
        .andExpect(jsonPath("$.content.[1].city.name", is("Teste")))
        .andExpect(jsonPath("$.content.[1].city.state", is("SP")))
        .andExpect(jsonPath("$.content.[1].city.regional", is(true)))
        .andExpect(jsonPath("$.content.[1].email", is("mail@mail.com")))
        .andExpect(jsonPath("$.content.[1].role", is("ROLE_ADMIN")))
        .andExpect(jsonPath("$.content.[2].id", is(3)))
	    .andExpect(jsonPath("$.content.[2].name", is("Jaguariuna")))
	    .andExpect(jsonPath("$.content.[2].city.id", is(1)))
        .andExpect(jsonPath("$.content.[2].city.name", is("Teste")))
        .andExpect(jsonPath("$.content.[2].city.state", is("SP")))
        .andExpect(jsonPath("$.content.[2].city.regional", is(true)))
        .andExpect(jsonPath("$.content.[2].email", is("mail@mail.com")))
        .andExpect(jsonPath("$.content.[2].role", is("ROLE_USUARIO")))
        .andExpect(jsonPath("$.content.[3].id", is(4)))
	    .andExpect(jsonPath("$.content.[3].name", is("Poços de Caldas")))
	    .andExpect(jsonPath("$.content.[3].city.id", is(1)))
        .andExpect(jsonPath("$.content.[3].city.name", is("Teste")))
        .andExpect(jsonPath("$.content.[3].city.state", is("SP")))
        .andExpect(jsonPath("$.content.[3].city.regional", is(true)))
        .andExpect(jsonPath("$.content.[3].email", is("mail@mail.com")))
        .andExpect(jsonPath("$.content.[3].role", is("ROLE_ADMIN")));
    }
	
	@Test
    public void findByIdNotFoundError() throws Exception {
        when(this.userService.byId(1l)).thenThrow(new ObjectNotFoundException(1l, User.class.getName()));
        this.mockMvc.perform(get(BY_ID).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("timestamp", notNullValue()))
            .andExpect(jsonPath("status", is(404)))
            .andExpect(jsonPath("message", is("Registro com id 1 não encontrado(a)")))
            .andExpect(jsonPath("error", is("No row with the given identifier exists: [br.org.rpf.cagef.entity.User#1]")))
            .andExpect(jsonPath("path", is(BY_ID)));
    }
	
	@Test
    public void findByIdSuccessTest() throws Exception {
		when(this.userService.byId(1l)).thenReturn(UserServiceImplTest.generateUserAdmin());
        this.mockMvc.perform(get(BY_ID).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("id", is(1)))
            .andExpect(jsonPath("name", is("Teste")))
            .andExpect(jsonPath("city.id", is(1)))
            .andExpect(jsonPath("city.name", is("Teste")))
            .andExpect(jsonPath("city.state", is("SP")))
            .andExpect(jsonPath("city.regional", is(true)))
            .andExpect(jsonPath("email", is("mail@mail.com")))
            .andExpect(jsonPath("role", is("ROLE_ADMIN")));
    }
	
	@Test
    public void saveBadRequestError() throws Exception {
        when(this.userService.save(any(UserDTO.class))).thenThrow(new DataIntegrityViolationException("Error"));
		this.mockMvc.perform(post(URL)
				.content(new ObjectMapper().writeValueAsString(UserServiceImplTest.generateUserDto())).contentType(
						MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("timestamp", notNullValue()))
            .andExpect(jsonPath("status", is(400)))
            .andExpect(jsonPath("message", is("Não é possível remover este registro pois está sendo utilizado")))
            .andExpect(jsonPath("error", is("Error")))
            .andExpect(jsonPath("path", is(URL)));
    }
	
	@Test
    public void saveSuccessTest() throws Exception {
		when(this.userService.save(any(UserDTO.class))).thenReturn(UserServiceImplTest.generateUserAdmin());
		this.mockMvc.perform(post(URL)
				.content(new ObjectMapper().writeValueAsString(UserServiceImplTest.generateUserDto())).contentType(
						MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(header().exists("location"))
            .andExpect(header().string("location", endsWith(BY_ID)));
    }
	
	@Test
    public void updateNotFoundError() throws Exception {
        when(this.userService.update(eq(1l), any(UserDTO.class))).thenThrow(new ObjectNotFoundException(1l, User.class.getName()));
        this.mockMvc.perform(put(BY_ID)
				.content(new ObjectMapper().writeValueAsString(UserServiceImplTest.generateUserDto())).contentType(
						MediaType.APPLICATION_JSON))
	        .andExpect(status().isNotFound())
	        .andExpect(jsonPath("timestamp", notNullValue()))
	        .andExpect(jsonPath("status", is(404)))
	        .andExpect(jsonPath("message", is("Registro com id 1 não encontrado(a)")))
            .andExpect(jsonPath("error", is("No row with the given identifier exists: [br.org.rpf.cagef.entity.User#1]")))
	        .andExpect(jsonPath("path", is(BY_ID)));

    }
	
	@Test
    public void updateSuccessTest() throws Exception {
		when(this.userService.update(eq(1l), any(UserDTO.class))).thenReturn(UserServiceImplTest.generateUserAdmin());
		this.mockMvc.perform(put(BY_ID)
				.content(new ObjectMapper().writeValueAsString(UserServiceImplTest.generateUserDto())).contentType(
						MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());
    }
	
	@Test
    public void removeotFoundError() throws Exception {
		doThrow(new ObjectNotFoundException(1l, User.class.getName())).when(this.userService).remove(1l);
        this.mockMvc.perform(delete(BY_ID)
				.content(new ObjectMapper().writeValueAsString(UserServiceImplTest.generateUserDto())).contentType(
						MediaType.APPLICATION_JSON))
	        .andExpect(status().isNotFound())
	        .andExpect(jsonPath("timestamp", notNullValue()))
	        .andExpect(jsonPath("status", is(404)))
	        .andExpect(jsonPath("message", is("Registro com id 1 não encontrado(a)")))
            .andExpect(jsonPath("error", is("No row with the given identifier exists: [br.org.rpf.cagef.entity.User#1]")))
	        .andExpect(jsonPath("path", is(BY_ID)));

    }
	
	@Test
    public void removeSuccessTest() throws Exception {
		doNothing().when(this.userService).remove(1l);
		this.mockMvc.perform(delete(BY_ID)
				.content(new ObjectMapper().writeValueAsString(UserServiceImplTest.generateUserDto())).contentType(
						MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());
    }
}
