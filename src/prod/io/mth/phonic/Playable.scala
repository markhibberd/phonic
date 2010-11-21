package io.mth.phonic

import java.net.URL
import com.sun.speech.freetts.VoiceManager
import io.mth.lever.Lever._
import java.io.Closeable
import javax.sound.sampled._

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
      val lineout = line(in)
      using2(in, line2closeable(lineout)) { i => o =>
        pipe(i, lineout, 1024 * 4096)
      }
    }

    override def toString = "play [" + url + "]"

    def line(in: AudioInputStream) = {
      val format = in.getFormat
      val info = lineinfo(format)
      val line = AudioSystem.getLine(info).asInstanceOf[SourceDataLine]
      line.open(format)
      line.start
      line
    }

    def lineinfo(format: AudioFormat) = new DataLine.Info(classOf[SourceDataLine], format)

    implicit def line2closeable(l: SourceDataLine) = new Closeable {
      def close = {
        l.drain
        l.close
      }
    }
  }


}