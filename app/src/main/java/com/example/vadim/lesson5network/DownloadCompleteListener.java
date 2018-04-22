package com.example.vadim.lesson5network;

import java.util.ArrayList;

public interface DownloadCompleteListener {
  void downloadComplete(ArrayList<Repository> repository);
}
