package io.mth.phonic

import java.util.concurrent.LinkedBlockingQueue

class Phonic {
  private val in = new LinkedBlockingQueue[Playable]
  private var audio: Option[Thread] = None

  def running = synchronized {
    audio.isDefined
  }

  def start = synchronized {
    if (!running) {
      val runner = new Runnable {
        def run: Unit =
          while (true)
            try {
              in.take.play
            } catch {
              case e: InterruptedException => return
            }
      }
      val t = new Thread(runner)
      t.setDaemon(true)
      t.start
      audio = Some(t)
    } else {
      throw new IllegalStateException("Audio system is already running.")
    }
  }

  def stop = synchronized {
    if (running) {
      audio.get.interrupt
      audio = None
    } else {
      throw new IllegalStateException("Audio system is not running.")
    }
  }

  def play(p: Playable) =
    if (running) {
      in.add(p)
    } else {
      throw new IllegalStateException("Audio system is not running.")
    }
}
 