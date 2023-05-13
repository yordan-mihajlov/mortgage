package bg.fmi.mortgage.controllers;


import bg.fmi.mortgage.models.Result;
import bg.fmi.vaultmanagerclient.component.VaultManagerProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/")
public class CalculatorController {

    @Autowired
    private VaultManagerProvider vaultManagerProvider;

    @GetMapping("/calculate")
    public ResponseEntity<Result> calculate(
            @NotBlank @Param("period") Integer period,
            @NotBlank @Param("amount") Double amount,
            @NotBlank @Param("country") String country
    ) {
        double interestRate = Double.parseDouble(vaultManagerProvider.getProperties().get("bankMortgage").get("interestRate"));
        String countryCoefficientString = vaultManagerProvider.getProperties().get("bankCommon").get("countryCoefficient." + country);
        if (countryCoefficientString == null) {
            countryCoefficientString = vaultManagerProvider.getProperties().get("bankCommon").get("countryCoefficient.OTHER");
        }
        Double countryCoefficient = Double.parseDouble(countryCoefficientString);


        double totalAmount = amount * Math.pow(1 + (interestRate/100),  period) * countryCoefficient;
        double monthlyPayment = totalAmount / (period * 12);

        Result result = new Result();
        result.setMonthlyPayment(BigDecimal.valueOf(monthlyPayment));
        result.setTotalPayment(BigDecimal.valueOf(totalAmount));
        return ResponseEntity.ok(result);
    }
}
