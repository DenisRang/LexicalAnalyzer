import java.io.*;

public class Main {
    private static final String OUTPUT_FILE_NAME = "out.txt";

    public static void main(String[] argv) throws IOException {
        LexicalAnalyzer lexer = new LexicalAnalyzer();
        Token token;
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(OUTPUT_FILE_NAME)))) {
            do {
                token = lexer.next();
                if (token == null) continue;
                writer.append(token.stringInfo() + " \n");
            } while (token == null || !token.getValue().equals(Token.TokenType.Eof.lexeme));
            lexer.closeBufferedReader();

        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }
}
