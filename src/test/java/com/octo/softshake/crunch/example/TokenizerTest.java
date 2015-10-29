package com.octo.softshake.crunch.example;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.octo.softshake.crunch.example.Tokenizer;
import org.apache.crunch.Emitter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class TokenizerTest {
  @Mock
  private Emitter<String> emitter;

  @Test
  public void testProcess() {
    Tokenizer splitter = new Tokenizer();
    splitter.process("  foo.  bar ", emitter);

    verify(emitter).emit("foo");
    verify(emitter).emit("bar");
    verifyNoMoreInteractions(emitter);
  }

  @Test
  public void testAccent() {
    Tokenizer splitter = new Tokenizer();
    splitter.process(" détachant peut-être son intérêt", emitter);

    verify(emitter).emit("détachant");
    verify(emitter).emit("peut-être");
    verify(emitter).emit("son");
    verify(emitter).emit("intérêt");
    verifyNoMoreInteractions(emitter);
  }

}
