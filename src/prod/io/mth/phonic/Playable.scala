package io.mth.phonic

import java.net.URL
import com.sun.speech.freetts.VoiceManager
import javax.sound.sampled.{SourceDataLine, DataLine, AudioSystem}

trait Playable {
  def play

  def :>:(p: Playable) = {
    val outer = this
    new Playable {
      def play = {
        p.play
        outer.play
      }
    }
  }

  def :<:(p: Playable) = {
    val outer = this
    new Playable {
      def play = {
        outer.play
        p.play
      }
    }
  }
}

object Playable {
  def sayAs(voice: String)(msg: String) = new Playable {
    def play = {
      // FIX nasty hack
        System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory,com.sun.speech.freetts.en.us.cmu_time_awb.AlanVoiceDirectory")
      val manager = VoiceManager.getInstance()

      val v = manager.getVoice(voice)
      if (v == null)
        throw new IllegalArgumentException("No voice found [" + voice + "]");
      v.allocate
      v.speak(msg)
      v.deallocate
    }

    override def toString = "say [" + msg + "]"
  }

  def say(s: String) = sayAs("kevin16")(s)

  def play(url: URL) = new Playable {
    def play = {
      val in = AudioSystem.getAudioInputStream(url)
      val format = in.getFormat
      val info = new DataLine.Info(classOf[SourceDataLine], format)
      val line = AudioSystem.getLine(info).asInstanceOf[SourceDataLine]
      line.open(format)
      line.start
      try {
        val buffer = new Array[Byte](1024 * 4096)
        (Iterator continually (in.read(buffer, 0, buffer.length))).
                takeWhile(_ != -1).
                foreach(line.write(buffer, 0, _))
      } finally {
        line.drain
        line.close
      }

    }

    override def toString = "play [" + url + "]"
  }
}