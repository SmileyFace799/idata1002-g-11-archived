module no.ntnu.idatx2001.g11 {
    requires transitive javafx.graphics;
    requires transitive javafx.controls;
    requires javafx.fxml;

    opens no.ntnu.idatx2001.g11.controllers to javafx.fxml;
    exports no.ntnu.idatx2001.g11;
    exports no.ntnu.idatx2001.g11.controllers;
    exports no.ntnu.idatx2001.g11.enums;
    exports no.ntnu.idatx2001.g11.exceptions;
    exports no.ntnu.idatx2001.g11.generics;
    exports no.ntnu.idatx2001.g11.logic;
    exports no.ntnu.idatx2001.g11.usersaves;
    opens no.ntnu.idatx2001.g11;
}
