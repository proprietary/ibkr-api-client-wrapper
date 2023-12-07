package net.zelcon.ibkr_api_client_wrapper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;

import net.zelcon.ibkr_api_client_wrapper.response_types.IBResponse;

public class IBReqPublisher extends SubmissionPublisher<IBResponse> {
    public IBReqPublisher(ExecutorService exec) {
        super(exec, Flow.defaultBufferSize());
    }
}