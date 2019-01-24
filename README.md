# Zero Lombok

This is an experimental project to supply source generated POJO's
with a builder, `toString`, `equals`, `hashCode` other things without
having to manipulate byte code (ie: lombok).


## Why?

Leave my byte code alone! For more reasons than one, I abhore libraries
that manipulate my (non-test) source byte code. On the other hand:
I fully appreciate utilizing code that I don't have to write, which what
libraries like lombok provide.

Why not achieve the best of both worlds? Let's generate legit source code
from source time annotations + processing!
