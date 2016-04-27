package pagerank_one;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
//import org.apache.log4j.BasicConfigurator;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.Counters;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.util.StringUtils;

//https://hadoop.apache.org/docs/r2.5.2/hadoop-mapreduce-client/hadoop-mapreduce-client-core/MapReduceTutorial.html

public class Simple_PageRank {

  public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
    //BasicConfigurator.configure();
    GenericOptionsParser optionParser = new GenericOptionsParser(conf, args);
    String[] remainingArgs = optionParser.getRemainingArgs();

    for (int i = 0; i < 5; ++i){

      Job job = Job.getInstance(conf, "simple_pagerank");
      //Set Jar Class
      job.setJarByClass(pagerank_one.Simple_PageRank.class);
      //Set Maper and Reducer
      job.setMapperClass(NodeMapper.class);
      job.setReducerClass(NodeResidualReducer.class);
      //write key of type long
      job.setOutputKeyClass(LongWritable.class);
      //write value of type Double
      job.setOutputValueClass(Text.class);
      //Input and output fed in from the arguments S3 folders
      if (i == 0){
        FileInputFormat.addInputPath(job, new Path(remainingArgs[0]));
      }
      else{
        FileInputFormat.addInputPath(job, new Path(remainingArgs[1]+i));

      }
        FileOutputFormat.setOutputPath(job, new Path(remainingArgs[1]+(i+1)));  
        
      job.waitForCompletion(true);
      Counters counters = job.getCounters();
      Counter c1 = counters.findCounter(CounterEnums.LATEST_RESIDUAL);
      float residual_updated_val = ((float)(c1.getValue()/685229));//100000000);
      float disp = ((float)(residual_updated_val/100000000));
      System.out.println((i) + "-Residual's Updated Val is: " + disp);
    }
    System.exit(0);
  }
}