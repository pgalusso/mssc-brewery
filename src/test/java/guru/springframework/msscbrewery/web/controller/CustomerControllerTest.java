package guru.springframework.msscbrewery.web.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.msscbrewery.services.CustomerService;
import guru.springframework.msscbrewery.web.model.CustomerDto;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(CustomerController.class)
public class CustomerControllerTest {

    @MockBean
    CustomerService customerService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    CustomerDto validCustomer;

    @Before
    public void setUp() {
        validCustomer = CustomerDto.builder().id(UUID.randomUUID())
                .name("Pepito Gomez")
                .build();
    }

    @Test
    public void getCustomer() throws Exception {
        given(customerService.getCustomerById(any(UUID.class))).willReturn(validCustomer);

        mockMvc.perform(get("/api/v1/customer/" + validCustomer.getId().toString()).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id", is(validCustomer.getId().toString())))
                .andExpect(jsonPath("$.name", is(validCustomer.getName())));
    }

    @Test
    public void handlePost() throws Exception {
        CustomerDto validDto = validCustomer;
        validDto.setId(null);
        CustomerDto savedDto = CustomerDto.builder().id(UUID.randomUUID()).name("Mengano Cualquiera").build();
        String customerDtoJson = objectMapper.writeValueAsString(savedDto);

        given(customerService.saveNewCustomer(any(CustomerDto.class))).willReturn(savedDto);

        mockMvc.perform(post("/api/v1/customer/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(customerDtoJson))
                .andExpect(status().isCreated());
    }

    @Test
    public void handleUpdate() throws Exception {
        CustomerDto validDto = this.validCustomer;
        String customerDtoJson = objectMapper.writeValueAsString(validDto);

        mockMvc.perform(put("/api/v1/customer/" + validDto.getId())
        .contentType((MediaType.APPLICATION_JSON))
        .content(customerDtoJson))
                .andExpect(status().isNoContent());

        then(customerService).should().updateCustomer(any(), any());
    }

    @Test
    public void handleDeleteById() throws Exception {
        CustomerDto validDto = this.validCustomer;

        mockMvc.perform(delete("/api/v1/customer/" + validDto.getId())
        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
