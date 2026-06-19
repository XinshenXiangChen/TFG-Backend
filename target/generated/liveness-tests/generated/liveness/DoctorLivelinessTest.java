package generated.liveness;

import edu.upc.fib.inlab.imp.kse.logics.logicschema.domain.*;
import edu.upc.fib.inlab.imp.kse.logics.logicschema.services.parser.LogicSchemaWithIDsParser;
import edu.upc.fib.inlab.imp.kse.reasoner.Goal;
import edu.upc.fib.inlab.imp.kse.reasoner.Reasoner;
import edu.upc.fib.inlab.imp.kse.reasoner.ReasonerProperties;
import edu.upc.fib.inlab.imp.kse.reasoner.ReasonerResult;
import edu.upc.fib.inlab.imp.kse.reasoner.SATResult;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DoctorLivelinessTest {

    private static final String SCHEMA_PATH = "target/generated/schema.dl";

    @Test
    void doctorIsSatisfiable() throws java.io.IOException {
        String schema = Files.readString(Path.of(SCHEMA_PATH), StandardCharsets.UTF_8);
        LogicSchema logicSchema = new LogicSchemaWithIDsParser().parse(schema);
        Predicate predicate = logicSchema.getPredicateByName("Doctor");
        List<Term> terms = new java.util.ArrayList<>();
        for (int i = 0; i < predicate.getArity(); i++) terms.add(new Variable("Var" + i));
        Goal goal = new Goal(new OrdinaryLiteral(new Atom(predicate, terms)));
        Reasoner reasoner = new Reasoner(logicSchema);
        ReasonerProperties properties = ReasonerProperties.builder().notifyEvents(false).build();
        ReasonerResult result = reasoner.isSatisfiable(goal, properties);
        assertThat(result).isInstanceOf(SATResult.class);
    }
}
