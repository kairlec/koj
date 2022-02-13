package com.kairlec.koj.sandbox.ffi.tools

import jdk.incubator.foreign.CLinker
import jdk.incubator.foreign.SymbolLookup

val linker = CLinker.getInstance()
val loader = SymbolLookup.loaderLookup()

