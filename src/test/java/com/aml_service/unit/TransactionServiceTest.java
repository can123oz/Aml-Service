package com.aml_service.unit;

import com.aml_service.model.Transaction;
import com.aml_service.model.TransactionOutbox;
import com.aml_service.repository.OutboxRepository;
import com.aml_service.repository.TransactionRepository;
import com.aml_service.service.TransactionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@Tag("unit")
@DisplayName("TransactionService Unit Tests")
public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private OutboxRepository outboxRepository;
    @Mock
    private ObjectMapper mapper;

    @Captor
    private ArgumentCaptor<TransactionOutbox> outboxArgumentCaptor;

    private TransactionService service;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        service = new TransactionService(transactionRepository, outboxRepository, mapper);
    }

    @DisplayName("Should process transaction successfully")
    @Test
    public void shouldSuccessfulProcessTransaction() throws JsonProcessingException {
        // Given
        Transaction model = new Transaction();
        model.setId("id");
        Transaction entity = model;
        String transactionString = "";
        TransactionOutbox outbox = new TransactionOutbox(model.getId(), transactionString);

        // When
        when(transactionRepository.saveAndFlush(model)).thenReturn(entity);
        when(mapper.writeValueAsString(entity)).thenReturn(transactionString);

        service.processTransaction(model);

        // Then
        verify(outboxRepository, times(1)).saveAndFlush(any());
        verify(transactionRepository, times(1)).saveAndFlush(model);

        then(outboxRepository).should().saveAndFlush(outboxArgumentCaptor.capture());
        TransactionOutbox capturedEntity = outboxArgumentCaptor.getValue();
        assertThat(capturedEntity.getEvent()).isEqualTo(outbox.getEvent());
    }

    @DisplayName("Should Throw JsonProcessingException")
    @Test
    public void shouldJsonProcessingException() throws JsonProcessingException {
        // Given
        Transaction model = new Transaction();
        model.setId("id");
        Transaction entity = model;

        // When
        when(transactionRepository.saveAndFlush(model)).thenReturn(entity);
        when(mapper.writeValueAsString(entity)).thenThrow(JsonProcessingException.class);

        assertThatThrownBy(() -> service.processTransaction(model))
                .isInstanceOf(RuntimeException.class);

        // Then
        verify(outboxRepository, never()).saveAndFlush(any());
        verify(transactionRepository, times(1)).saveAndFlush(model);
    }


}
