# Colonel

Yet another command framework? Yes, but this one is different.

This framework was created with commands for _Minecraft: Java Edition_ in mind, but it can extend to other platforms as well.
The main issue with alternative command frameworks is their lack of flexibility and/or huge amounts of setup work.

Colonel tries to be fast to setup, easy to use and most importantly still give YOU control. While we have some opiniated tools to help you,
you can always fall back to the basics. You have full control over the argument parsing and command execution.

## Let's get started

We'd like to think that a `command` is raw input given by someone. It consists of two parts: the `path` and the `arguments`.
The `path` decides which handler should be used and how the `arguments` should be interpreted. The `arguments` are context information. 

### Creating a command

```
Colonel<Person> colonel = new Colonel<>();

colonel.builder()
    .path("ping")
    .executes((ctx) -> ctx.source().sendMessage("pong!"));
```

You don't need to use the builder, you can also register your own `CommandHandler` whith all the horns and bells 
you need directly at a given path with
```
colonel.register("ping", new MyPingHandler());
```

The path may also contain spaces for sub-commands and these will be efficiently registered in the command tree.

### Executing a command

```
colonel.dispatch(person, "ping");
```

Easy right?

### How to define parameters

For a given command, arguments are always parsed from left to right. You can also use information of the previous
argument when parsing the next one.

```
colonel.builder()
    .path("square")
    .string("value", (ctx, input) -> Integer.parseInt(input))
    .executes((ctx) -> ctx.source().sendMessage(ctx.argument("value") * ctx.argument("value")));
```

Notice that YOU decide how to parse the argument input to a Java object. You must however define how the input should be
structured. You can use `string` which takes the next word or quoted text and `greedy` which takes all the remaining input.

## Now hold up, this is still a lot of work?

Yes, but we have some tools to help you. Additionally, to defining commands in a functional style, you can also use
annotations which even add more built-in functionality.

```
class CommandContainer {

    @Command("square")
    public void square(@Source Person sender, @Parameter int value) {
        sender.sendMessage(value * value);
    }

}

AnnotatedColonel<Person> colonel = new AnnotatedColonel<>();
colonel.registerAll(new CommandContainer());
```

Colonel will provide parsers for all Java primitives and some other types like LocalDate, LocalTime... by default, but you can override these with

```
colonel.registry().registerParameterParser(Integer.class, (ctx, input) -> Integer.parseInt(input));
```

or with

```
class CommandContainer {

    @Parser("shape")
    public Shape shape(CommandContext<Person> ctx, String input) {
        return Shape.parse(input)
    }

}

colonel.registerAll(new CommandContainer());
```

You can put multiple commands, parsers and completers all in the same class. The parsers and completers will be registered before the commands.

## What if the input can't be parsed?

If something goes wrong, just throw an exception. If you need some fallback execution use this
```
throw new CommandMiddlewareException(() -> {
    ctx.source().sendMessage("The input 'foo' cannot be parsed to an integer.");
});
```

Colonel will run the handler of the first error that occurs when a command is executed.


