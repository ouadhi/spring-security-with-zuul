package com.f1soft.departmentservice.controllertest;

import com.f1soft.departmentservice.controller.DepartmentController;
import com.f1soft.departmentservice.entities.Department;
import com.f1soft.departmentservice.requestDTO.DepartmentSetupDTO;
import com.f1soft.departmentservice.requestDTO.UpdatedDepartmentDTO;
import com.f1soft.departmentservice.service.DepartmentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static com.f1soft.departmentservice.constants.WebResourceConstants.BASE_API;
import static com.f1soft.departmentservice.constants.WebResourceConstants.DepartmentController.BASE_API_DEPARTMENT;
import static com.f1soft.departmentservice.constants.WebResourceConstants.DepartmentController.DEPARTMENTCRUD.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@WebMvcTest(DepartmentController.class)
public class DepartmentControllerTest {

    @MockBean
    private DepartmentService departmentService;

    @Autowired
    MockMvc mockMvc;

    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));


    @Test
    public void departmentCrud() throws Exception {
        save_ShouldSaveDepartment();
        retrieve_ShouldRetrieveDepartments();
        delete_ShouldDeleteDepartment();

    }

    @Test
    public void save_ShouldSaveDepartment() throws Exception {
        String URL = BASE_API + BASE_API_DEPARTMENT + SAVE;
        System.out.println(URL);

        DepartmentSetupDTO departmentSetupDTO = DepartmentSetupDTO.builder()
                .departmentName("Surgical")
                .code("SRG")
                .status('Y')
                .build();

        given(departmentService.addDepartment(departmentSetupDTO)).willReturn(getDepartment());

        mockMvc.perform(MockMvcRequestBuilders.post(URL)
                .contentType(APPLICATION_JSON_UTF8)
                .content(writeObjectToJson(departmentSetupDTO)))
                .andExpect(status().isOk())
                .andReturn();

        verify(departmentService).addDepartment(departmentSetupDTO);
    }

    @Test
    public void retrieve_ShouldRetrieveDepartments() throws Exception {
        String URL = BASE_API + BASE_API_DEPARTMENT + RETRIEVE;
        List<Department> departmentList = new ArrayList<>();
        departmentList.add(getDepartment());

        given(departmentService.fetchAllDepartment()).willReturn(departmentList);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(URL))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].departmentName",
                        Matchers.is("Surgical")))
                .andReturn();
        System.out.println(mvcResult.getResponse().getContentAsString());

        verify(departmentService).fetchAllDepartment();
    }

    @Test
    public void delete_ShouldDeleteDepartment() throws Exception {
        String URL = BASE_API + BASE_API_DEPARTMENT + DELETE;

        given(departmentService.deleteDepartment(1L)).willReturn(getDeletedDepartment());

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post(URL, 1L)
                .contentType(APPLICATION_JSON_UTF8))
                .andExpect(MockMvcResultMatchers.jsonPath("status",
                        Matchers.is("D")))
                .andExpect(status().isOk()).andReturn();

        System.out.println(mvcResult.getResponse().getContentAsString());
        verify(departmentService).deleteDepartment(1L);
    }

    @Test
    public void update_ShouldUpdateDepartment() throws Exception {
        String URL = BASE_API + BASE_API_DEPARTMENT + UPDATE;

        UpdatedDepartmentDTO updatedDepartmentSetupDTO = UpdatedDepartmentDTO.builder()
                .id(1L)
                .departmentName("Surgical")
                .code("SRG")
                .status('Y')
                .build();

        given(departmentService.updateDepartment(updatedDepartmentSetupDTO)).willReturn(getDepartment());

        System.out.println(getDepartment());

        mockMvc.perform(MockMvcRequestBuilders.post(URL)
                .contentType(APPLICATION_JSON_UTF8)
                .content(writeObjectToJson(updatedDepartmentSetupDTO)))
                .andExpect(status().isOk());

        verify(departmentService).updateDepartment(updatedDepartmentSetupDTO);

    }


    public Department getDepartment() {
        Department savedDepartment = Department.builder()
                .id(1L)
                .departmentName("Surgical")
                .code("SRG")
                .status('Y')
                .build();
        return savedDepartment;
    }

    public Department getDeletedDepartment() {
        Department savedDepartment = Department.builder()
                .id(1L)
                .departmentName("Surgical")
                .code("SRG")
                .status('D')
                .build();
        return savedDepartment;
    }


    private String writeObjectToJson(final Object obj) {
        ObjectMapper objectMapper=new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

}



