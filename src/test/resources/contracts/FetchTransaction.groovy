package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Should return a transaction for a valid ID")

    request {
        method GET()
        url("/api/v1/transactions?reference=test-reference")
    }

    response {
        status 200
        body([
                reference       : "test-reference",
                amlResult: "PASS",
                status   : "TEST"
        ])
        headers {
            contentType(applicationJson())
        }
    }
}