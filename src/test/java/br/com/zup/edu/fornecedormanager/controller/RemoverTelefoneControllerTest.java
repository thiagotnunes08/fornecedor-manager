package br.com.zup.edu.fornecedormanager.controller;

import br.com.zup.edu.fornecedormanager.model.Fornecedor;
import br.com.zup.edu.fornecedormanager.model.Telefone;
import br.com.zup.edu.fornecedormanager.repository.FornecedorRepository;
import br.com.zup.edu.fornecedormanager.repository.TelefoneRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(printOnlyOnFailure = false)
class RemoverTelefoneControllerTest {

    @Autowired
    private TelefoneRepository telefoneRepository;
    @Autowired
    private FornecedorRepository fornecedorRepository;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mockMvc;

    private Telefone telefone;

    private Fornecedor fornecedor;

    @BeforeEach
    void setUp() {
        telefoneRepository.deleteAll();
        fornecedorRepository.deleteAll();

        this.fornecedor = new Fornecedor("THIAGO", "SOJA", "AGRO POP");
        fornecedorRepository.save(fornecedor);

        this.telefone = new Telefone(fornecedor, "034", "9999-8888");
        telefoneRepository.save(telefone);
    }

    @Test
    @DisplayName("deve remover telefone de um fonecedor")
    void test1() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete("/fornecedores/{idFornecedor}/telefones/{idTelefone}", fornecedor.getId(), telefone.getId()).contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isNoContent());

        assertFalse(telefoneRepository.existsById(telefone.getId()), "nao deve existir este telefone");

    }

    @Test
    @DisplayName("nao deve remover telefone, pois fornecedor nao existe no sistema")
    void test2() throws Exception {

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete("/fornecedores/{idFornecedor}/telefones/{idTelefone}", Integer.MAX_VALUE, telefone.getId()).contentType(MediaType.APPLICATION_JSON);
        Exception resolvedException = mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isNotFound()).andReturn().getResolvedException();

        assertNotNull(resolvedException);
        assertEquals(ResponseStatusException.class, resolvedException.getClass());
        assertEquals("Fornecedor nao cadastrado", ((ResponseStatusException) resolvedException).getReason());
    }

    @Test
    @DisplayName("nao deve remover telefone inexistente")
    void test3() throws Exception {

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete("/fornecedores/{idFornecedor}/telefones/{idTelefone}", fornecedor.getId(), Integer.MAX_VALUE).contentType(MediaType.APPLICATION_JSON);
        Exception resolvedException = mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isNotFound()).andReturn().getResolvedException();

        assertNotNull(resolvedException);
        assertEquals(ResponseStatusException.class, resolvedException.getClass());
        assertEquals("Telefone nao cadastrado", ((ResponseStatusException) resolvedException).getReason());
    }

    @Test
    @DisplayName("telefone nao pertece a este fornecedor")
    void test4() throws Exception {

        Fornecedor lucas = new Fornecedor("lucas", "milho", "new Hollad");
        fornecedorRepository.save(lucas);

        Telefone radim = new Telefone(lucas, "044", "99999-88888");
        telefoneRepository.save(radim);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/fornecedores/{idFornecedor}/telefones/{idTelefone}", fornecedor.getId(), radim.getId()).contentType(MediaType.APPLICATION_JSON);

        Exception resolvedException = mockMvc.perform(requestBuilder).andExpect(MockMvcResultMatchers.status().isUnprocessableEntity()).andReturn().getResolvedException();

        assertNotNull(resolvedException);
        assertEquals(ResponseStatusException.class, resolvedException.getClass());
        assertEquals("Este telefone nao pertence ao fornecedor", ((ResponseStatusException) resolvedException).getReason());


    }

}