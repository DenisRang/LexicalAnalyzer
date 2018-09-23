public class Token {
    public enum TokenType {
        //Keywords
        And("and"),
        As("as"),
        Assert("assert"),
        Asr("asr"),
        Begin("begin"),
        Bool("bool"),
        Class("class"),
        Constraint("constraint"),
        Do("do"),
        Done("done"),
        DownTo("downto"),
        Else("else"),
        End("end"),
        Exception("exception"),
        External("external"),
        False("false"),
        For("for"),
        Fun("fun"),
        Function("function"),
        Functor("functor"),
        If("if"),
        In("in"),
        Include("include"),
        Inherit("inherit"),
        Initializer("initializer"),
        Land("land"),
        Lazy("lazy"),
        Let("let"),
        Lor("lor"),
        Lsl("lsl"),
        Lsr("lsr"),
        Lxor("lxor"),
        Match("match"),
        Max("max"),
        Method("method"),
        Min("min"),
        Mod("mod"),
        Module("module"),
        Mutable("mutable"),
        New("new"),
        NonRec("nonrec"),
        Object("object"),
        Of("of"),
        Open("open"),
        Or("or"),
        Private("private"),
        Raise("raise"),
        Rec("rec"),
        Ref("ref"),
        Sig("sig"),
        Struct("struct"),
        Then("then"),
        To("to"),
        True("true"),
        Try("try"),
        Type("type"),
        Val("val"),
        Virtual("virtual"),
        When("when"),
        While("while"),
        With("with"),

        //Operators
        Equality("==|="),
        Greater(">"),
        Less("<"),
        GreaterEqual(">="),
        LessEqual("<="),
        NotEqual("<>"),
        AssingmentOperator(":="),
        Dereference("!"),
        BoolenanAnd("&&"),
        BooleanOr("||"),
        BitAnd("&"),
        BitOr("|"),
        Plus("+"),
        PlusDot("+."),
        Minus("-"),
        MinusDot("-."),
        Multiply("*"),
        MultiplyDot("*."),
        Exponential("**"),
        Divide("/"),
        DivideDot("/."),
        StringConcat("^"),
        ListConcat("@"),
        FunctionalOp("->"),


        //Separators
        Comma(","),
        Dot("."),
        DoubleDot(".."),
        VariableAssignment("<-"),
        Cast(":>"),
        Ternaryquestion("?"),
        LeftSquaredBracket("["),
        SpecialLeftSB1("[<"), //SB stands for squared bracket
        SpecialLeftSB2("[>"),
        SpecialLeftSB3("[|"),
        RightSquaredBracket("]"),
        SpecialRightSB1("<]"),
        SpecialRightSB2(">]"),
        SpecialRightSB3("|]"),
        LeftBrace("{"),
        SpecialLeftBrace("{<"),
        RightBrace("}"),
        SpecialRightBrace(">}"),
        Colon(":"),
        DoubleColon("::"),
        Semicolon(";"),
        DoubleSemicolon(";;"),
        Underscore("_"),
        SingleQuote("'"),
        DoubleQuote("\""),
        Tilde("~"),
        BackTick("`"),
        Hashtag("#"),
        CommentSection("(* ... *)"),
        LeftParenth("("),
        RightParenth(")"),

        //Literals
        IntLiteral("intLiteral"),
        FloatLiteral("floatLiteral"),
        StringLiteral("stringLiteral"),
        CharLiteral("charLiteral"),

        //Identifier
        Identifier("identifier"),

        Eof("eof");


        public final String lexeme;

        TokenType(String pattern) {
            this.lexeme = pattern;
        }

        public String getPattern() {
            return lexeme;
        }
    }

    private static final int KEYWORDS_AMOUNT = 61; //number of keywords

    private TokenType type;
    private String value;


    Token(TokenType type, String value) {
        this.type = type;
        this.value = value;
    }

    Token(TokenType type) {
        this.type = type;
        this.value = type.lexeme;
    }

    /**
     * Сhecks if lexeme is a keyword
     *
     * @param lexeme - keyword or identifier
     * @return token - either a keyword token, or identifier token
     */
    public static Token keyword(String lexeme) {
        char ch = lexeme.charAt(0);
        if (ch >= 'A' && ch <= 'Z')
            return createIdentToken(lexeme); //keywords can't contain uppercase => it is an identifier
        for (TokenType tokenType : TokenType.values()) {
            if (tokenType.ordinal() >= KEYWORDS_AMOUNT) break;
            if (lexeme.equals(tokenType.lexeme)) return new Token(tokenType); //looking for this lexeme in array of reserved tokens
        }
        return createIdentToken(lexeme); //still not keyword => must be identifier
    }

    /**
     * This method creates new Identifier token
     *
     * @param lexeme - value of token
     * @return - created token
     */
    public static Token createIdentToken(String lexeme) {
        return new Token(TokenType.Identifier, lexeme);
    }

    /**
     * This method creates new Integer Literal token
     *
     * @param lexeme - value of token
     * @return - created token
     */
    public static Token createIntLiteralToken(String lexeme) {
        lexeme = lexeme.replaceAll("_", "");
        return new Token(TokenType.IntLiteral, lexeme);
    }

    /**
     * This method creates new Floating Point Literal token
     *
     * @param lexeme - value of token
     * @return - created token
     */
    public static Token createFloatLiteralToken(String lexeme) {
        lexeme = lexeme.replaceAll("_", "");
        return new Token(TokenType.FloatLiteral, lexeme);
    }

    /**
     * This method creates new Character Literal token
     *
     * @param lexeme - value of token
     * @return - created token
     */
    public static Token createCharLiteralToken(String lexeme) {
        return new Token(TokenType.CharLiteral, lexeme);
    }

    /**
     * This method creates new String Literal token
     *
     * @param lexeme - value if token
     * @return - created token
     */
    public static Token createStringLiteralToken(String lexeme) {
        return new Token(TokenType.StringLiteral, lexeme);
    }

    /**
     * This method returns information about token
     *
     * @return string that contains either type of token and its value, or just type
     */
    public String stringInfo() {
        return type + "      " + value;
    }

    @Override
    public String toString() {
        return type.lexeme;
    }

    public String getValue() {
        return value;
    }
}
