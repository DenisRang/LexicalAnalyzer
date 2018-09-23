import java.io.*;

public class LexicalAnalyzer {

    private final String letters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";  // lower and uppercase letters
    private final String digits = "1234567890"; // all digits
    private final String escapeSequence = "\\$\\^\\\\.\\*\\+\\?\\[\\]";   // escape characters
    private final String doublePart = "eEdDfF";

    private BufferedReader input;
    private char current_ch;                    // current character that is read
    private int position = 1;                   // current position of a cursor
    private String line = "";


    /**
     * while creating Lexical Analyzer it creates reader for the in.txt file
     *
     * @throws IOException
     */
    public LexicalAnalyzer() throws IOException {
        try {
            File inputFile = new File("in.txt");
            input = new BufferedReader(new FileReader(inputFile));
            nextChar();
        } catch (FileNotFoundException exception) {
            System.out.println(exception.getMessage());
        }
    }

    /**
     * method to close input file reader
     *
     * @throws IOException
     */
    public void closeBufferedReader() throws IOException {
        input.close();
    }

    /**
     * method to return next character
     *
     * @return
     */
    private void nextChar() throws IOException {
        position++;
        if (position >= line.length()) {
            try {
                line = input.readLine();
            } catch (IOException exception) {
                File outputFile = new File("out.txt");
                BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
                writer.write(exception.getMessage());
                writer.close();
                System.exit(1);
            }
            if (line != null) {
                line += "\n";
            } else {
                line = "\004";
            }
            position = 0;
        }
        current_ch = line.charAt(position);
    }

    /**
     * main analyzer method
     *
     * @return next token
     * @throws IOException
     */
    public Token next() throws IOException {
        do {
            if (isLetter(current_ch) || current_ch == '_') {
                String token = parseToken(letters + digits + '_');
                return Token.keyword(token);
            } else if (isDigit(current_ch)) {
                char first_char = current_ch;
                nextChar();
                String token = "";
                token += first_char;

                // for hexadecimal numbers
                if ((current_ch == 'x' || current_ch == 'X') && first_char == '0') {
                    token += parseToken(digits + "abcdefABCDEF_");
                    if (current_ch == '.') {
                        token += parseToken(digits + "abcdefABCDEF_");
                        if (current_ch == 'p' || current_ch == 'P') {
                            token += parseToken(digits + "_");
                            if (current_ch == '+' || current_ch == '-' || isDigit(current_ch))
                                token += parseToken(digits + "_");
                        } else
                            throw new InvalidToken("Invalid token is entered: hex double");
                        return Token.createFloatLiteralToken(token);
                    }
                    return Token.createIntLiteralToken(token);
                }

                // for binary numbers
                else if ((current_ch == 'b' || current_ch == 'B') && first_char == '0') {
                    token += parseToken("01_");
                    return Token.createIntLiteralToken(token);
                }
                // for either octal numbers or float literals which starts with 0
                else if (first_char == '0' && current_ch != '.' && current_ch != ',') {
                    token += parseToken("01234567_");
                    if (isDigit(current_ch)) {
                        token += parseToken(digits + "_");
                        if (doublePart.indexOf(current_ch) > -1 || current_ch == '.' || current_ch == ',')
                            return doubleHandle(token);
                        else
                            throw new InvalidToken("Invalid token is entered: int literal");
                    } else if (doublePart.indexOf(current_ch) > -1 || current_ch == '.' || current_ch == ',') {
                        return doubleHandle(token);
                    } else if (token.contains("8") || token.contains("9")) {
                        throw new InvalidToken("Invalid token is entered: int literal");
                    } else
                        return Token.createIntLiteralToken(token);
                } else if (current_ch != '.' && current_ch != ',') {
                    token += parseToken(digits + "_");
                    if (current_ch == '.' || current_ch == ',' || doublePart.indexOf(current_ch) > -1)
                        return doubleHandle(token);
                } else {
                    return doubleHandle(token);
                }
            } else if (current_ch == '\"') {
                String token = createStringLiteral();
                if (current_ch == '\"') {
                    nextChar();
                    return Token.createStringLiteralToken(token);
                } else {
                    throw new InvalidToken("Invalid string is entered");
                }
            } else switch (current_ch) {
                // escape sequence
                case ' ':
                case '\t':
                case '\r':
                case '\n':
                    nextChar();
                    break;

                case '\004':
                    return new Token(Token.TokenType.Eof);

                // char token
                case '\'':
                    char token = current_ch;
                    nextChar();
                    int a = escapeSequence.indexOf(current_ch);
                    if (current_ch == '\'') {
                        nextChar();
                        return Token.createCharLiteralToken("" + token);
                    } else if (token == '\\' && (a > -1)) {
                        String str = "" + token + current_ch;
                        nextChar();
                        nextChar();
                        return Token.createCharLiteralToken("" + str);
                    } else
                        throw new InvalidToken("Invalid char is entered");

                    // operators
                case '!':
                    nextChar();
                    return new Token(Token.TokenType.Dereference);
                case '^':
                    nextChar();
                    return new Token(Token.TokenType.StringConcat);
                case '@':
                    nextChar();
                    return new Token(Token.TokenType.ListConcat);

                // separators
                case ',':
                    nextChar();
                    return new Token(Token.TokenType.Comma);
                case '?':
                    nextChar();
                    return new Token(Token.TokenType.Ternaryquestion);
                case ']':
                    nextChar();
                    return new Token(Token.TokenType.RightSquaredBracket);
                case '}':
                    nextChar();
                    return new Token(Token.TokenType.RightBrace);
                case '~':
                    nextChar();
                    return new Token(Token.TokenType.Tilde);
                case '`':
                    nextChar();
                    return new Token(Token.TokenType.BackTick);
                case '#':
                    nextChar();
                    return new Token(Token.TokenType.Hashtag);
                case ')':
                    nextChar();
                    return new Token(Token.TokenType.RightParenth);

                // handle special cases
                default:
                    return checkSpecialCases(current_ch);
            }
        } while (true);
    }

    /**
     * this method checks if the current character is
     * one of the English letters of the alphabet both
     * lower and uppercase
     *
     * @param ch current character
     * @return
     */
    private boolean isLetter(char ch) {
        return letters.indexOf(ch) > -1;
    }

    /**
     * this method checks if the current character
     * is one of the digits
     *
     * @param ch current character
     * @return
     */
    private boolean isDigit(char ch) {
        return digits.indexOf(ch) > -1;
    }

    /**
     * this method finds the whole token
     *
     * @param possibleCharacters what kind of characters could be used in the token
     * @return
     */
    private String parseToken(String possibleCharacters) throws IOException {
        String token = "";
        do {
            token += current_ch;
            nextChar();
        } while (possibleCharacters.indexOf(current_ch) > -1);
        return token;
    }

    /**
     * method to create string literal
     * reads until meets " without \ before it or meets the end of file
     *
     * @return token
     * @throws IOException
     */
    private String createStringLiteral() throws IOException {
        String token = "";
        do {
            token += current_ch;
            nextChar();
            if (current_ch == '\\') {
                token += current_ch;
                nextChar();
                token += current_ch;
                nextChar();
            }
        } while (current_ch != '"' && current_ch != '\004');
        return token;
    }

    /**
     * check for special tokens like [>, [<, [|, ...
     *
     * @return Token
     */
    private Token checkSpecialCases(char first_char) throws IOException {
        nextChar();
        switch (first_char) {
            // operators
            case '=':
                if (current_ch == '=') {
                    nextChar();
                }
                nextChar();
                return new Token(Token.TokenType.Equality);
            case '<':
                switch (current_ch) {
                    case '=':
                        nextChar();
                        return new Token(Token.TokenType.LessEqual);
                    case '>':
                        nextChar();
                        return new Token(Token.TokenType.NotEqual);
                    case '-':
                        nextChar();
                        return new Token(Token.TokenType.VariableAssignment);
                    case ']':
                        nextChar();
                        return new Token(Token.TokenType.SpecialRightSB1);
                    default:
                        nextChar();
                        return new Token(Token.TokenType.Less);
                }
            case '>':
                switch (current_ch) {
                    case '=':
                        nextChar();
                        return new Token(Token.TokenType.GreaterEqual);
                    case ']':
                        nextChar();
                        return new Token(Token.TokenType.SpecialRightSB2);
                    case '}':
                        nextChar();
                        return new Token(Token.TokenType.SpecialRightBrace);
                    default:
                        nextChar();
                        return new Token(Token.TokenType.Greater);
                }
            case '&':
                if (current_ch == '&') {
                    nextChar();
                    return new Token(Token.TokenType.BoolenanAnd);
                } else {
                    nextChar();
                    return new Token(Token.TokenType.BitAnd);
                }
            case '|':
                switch (current_ch) {
                    case '|':
                        nextChar();
                        return new Token(Token.TokenType.BooleanOr);
                    case ']':
                        nextChar();
                        return new Token(Token.TokenType.SpecialRightSB3);
                    default:
                        nextChar();
                        return new Token(Token.TokenType.BitOr);
                }
            case '+':
                if (current_ch == '.') {
                    nextChar();
                    return new Token(Token.TokenType.PlusDot);
                } else {
                    nextChar();
                    return new Token(Token.TokenType.Plus);
                }
            case '-':
                switch (current_ch) {
                    case '.':
                        nextChar();
                        return new Token(Token.TokenType.MinusDot);
                    case '>':
                        nextChar();
                        return new Token(Token.TokenType.FunctionalOp);
                    default:
                        nextChar();
                        return new Token(Token.TokenType.Minus);
                }
            case '/':
                if (current_ch == '.') {
                    nextChar();
                    return new Token(Token.TokenType.DivideDot);
                } else {
                    nextChar();
                    return new Token(Token.TokenType.Divide);
                }
            case '*':
                switch (current_ch) {
                    case '.':
                        nextChar();
                        return new Token(Token.TokenType.MultiplyDot);
                    case '*':
                        nextChar();
                        return new Token(Token.TokenType.Exponential);
                    default:
                        nextChar();
                        return new Token(Token.TokenType.Multiply);
                }

                // separators
            case '.':
                if (current_ch == '.') {
                    nextChar();
                    return new Token(Token.TokenType.DoubleDot);
                } else {
                    nextChar();
                    return new Token(Token.TokenType.Dot);
                }
            case '[':
                switch (current_ch) {
                    case '<':
                        nextChar();
                        return new Token(Token.TokenType.SpecialLeftSB1);
                    case '>':
                        nextChar();
                        return new Token(Token.TokenType.SpecialLeftSB2);
                    case '|':
                        nextChar();
                        return new Token(Token.TokenType.SpecialLeftSB3);
                    default:
                        nextChar();
                        return new Token(Token.TokenType.LeftSquaredBracket);
                }
            case '{':
                if (current_ch == '<') {
                    nextChar();
                    return new Token(Token.TokenType.SpecialLeftBrace);
                } else {
                    nextChar();
                    return new Token(Token.TokenType.LeftBrace);
                }
            case ':':
                switch (current_ch) {
                    case '=':
                        nextChar();
                        return new Token(Token.TokenType.AssingmentOperator);
                    case '>':
                        nextChar();
                        return new Token(Token.TokenType.Cast);
                    case ':':
                        nextChar();
                        return new Token(Token.TokenType.DoubleColon);
                    default:
                        nextChar();
                        return new Token(Token.TokenType.Colon);
                }
            case ';':
                if (current_ch == ';') {
                    nextChar();
                    return new Token(Token.TokenType.DoubleSemicolon);
                } else {
                    nextChar();
                    return new Token(Token.TokenType.Semicolon);
                }
            case '(':
                if (current_ch == '*') {
                    return commentHandle();
                } else {
                    nextChar();
                    return new Token(Token.TokenType.LeftParenth);
                }
        }
        nextChar();
        return null;
    }


    /**
     * method to skip comments if occurred
     *
     * @return CommentSection token
     * @throws IOException
     */
    private Token commentHandle() throws IOException {
        char c1;
        char c2;
        do {
            nextChar();
            c1 = current_ch;
            nextChar();
            c2 = current_ch;
        } while (!(c1 == '*' && c2 == ')'));
        nextChar();
        return new Token(Token.TokenType.CommentSection);
    }

    /**
     * method to handle possible variations of double literals
     *
     * @param token
     * @return either float token or error
     * @throws IOException
     */
    private Token doubleHandle(String token) throws IOException {
        token += parseToken(digits + "_");
        char c;
        if (current_ch == 'e' || current_ch == 'E') {
            c = current_ch;
            token += "" + current_ch + c;
            nextChar();
            if (c == '+' || c == '-' && isDigit(current_ch)) {
                token += parseToken(digits + "_");
            } else if (isDigit(c)) {
                token += parseToken(digits + "_");
            } else {
                throw new InvalidToken("Invalid token is entered : double literal");
            }
            token += parseToken(digits + "_");
        }
        if (current_ch == 'd' || current_ch == 'f' || current_ch == 'D' || current_ch == 'F') {
            token += current_ch;
        }
        return Token.createFloatLiteralToken(token);
    }
}
