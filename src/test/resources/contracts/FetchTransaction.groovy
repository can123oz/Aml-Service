package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("Should return a transaction for a valid ID")

    request {
        method GET()
        url("/api/v1/transactions?id=db4647dd-812b-4cb0-a0f8-533a89a8903b")
    }

    response {
        status 200
        body([
                id       : "db4647dd-812b-4cb0-a0f8-533a89a8903b",
                amlResult: "SUCCESS",
                status   : "TEST"
        ])
        headers {
            contentType(applicationJson())
        }
    }
}