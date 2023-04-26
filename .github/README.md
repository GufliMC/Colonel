# Colonel

Yet another command framework? Yes, but this one is different.

This framework was created with commands for _Minecraft: Java Edition_ in mind, but it can extend to other platforms as well.
The main issue with alternative command frameworks is their lack of flexibility and/or huge amounts of setup work.

Colonel tries to be fast to setup, easy to use and most importantly still give YOU control. While we have some opiniated tools to help you,
you can always fall back to the basics. You have full control over the argument parsing and command execution.

## Let's get started

We'd like to think that a `command` is raw input given by someone. It consists of two parts: the `name` and the `arguments`.
The `name` decides what is executed and how the `arguments` should be interpreted. The `arguments` are context information. 

### Creating a command

```
Colonel<Person> colonel = new Colonel<>();

colonel.register("ping", builder -> builder
    .executes((ctx) -> ctx.source().sendMessage("pong!")
));
```

You don't need to use the builder, you can also provide your own `CommandHandler` whith all the horns and bells you need.

### Executing a command

```
colonel.dispatch(person, "ping");
```

### How to define parameters

For a given command, arguments are always parsed from left to right. You can also use information of the previous
argument when parsing the next one.

```
colonel.register("square", builder -> builder
    .word("value", (ctx, input) -> Argument.success(Integer.parseInt(input)))
    .executes((ctx) -> ctx.source().sendMessage(ctx.argument("value") * ctx.argument("value"))
));
```

Notice that YOU decide how to parse the argument input to a Java object. You must however define how the input should be
structured. You can use `word`, `string` which allows quoted text and `greedy` which takes all the remaining input.

## Now hold up, this is still a lot of work?

Yes, but we have some tools to help you. Additionally, to defining commands in a functional style, you can also use
annotations.

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

When using annotations you can use any Java primitive by default but you can override this or add custom types with


```
colonel.registerParameterParser(Integer.class, (ctx, input) -> Argument.success(Integer.parseInt(input)));
```

or with

```
class CommandContainer {

    @Parser("shape")
    public Shape integer(CommandContext<Person> ctx, String input) {
        return Shape.valueOf(input)
    }

}

colonel.registerAll(new CommandContainer());
```


