module com.univalle.sudoku {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.example.sudoku to javafx.fxml;
    opens com.example.sudoku.controller to javafx.fxml;
    opens com.example.sudoku.model to javafx.fxml;
    opens com.example.sudoku.view to javafx.fxml;

    exports com.example.sudoku;
    exports com.example.sudoku.controller;
    exports com.example.sudoku.model;
    exports com.example.sudoku.view;
    exports com.example.sudoku.events;

}
