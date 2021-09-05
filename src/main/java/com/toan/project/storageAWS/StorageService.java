package com.toan.project.storageAWS;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class StorageService {

    //    private final AmazonS3 space;
    private final AmazonS3 s3client;

    public StorageService() {
//        AWSCredentialsProvider awsCredentialsProvider = new AWSStaticCredentialsProvider(
//                new BasicAWSCredentials("AKIAVPWD6UOLSPEO7LVN", "DZX1YmjS4aVy9DEblbaOxWbD3tcFkOFbSa/tXrJC")
//        );
//        space = AmazonS3ClientBuilder.standard().withCredentials(awsCredentialsProvider).withEndpointConfiguration(
//                new AwsClientBuilder.EndpointConfiguration("sgp1.digitaloceanspaces.com", "sgp1")
//        ).build();

        AWSCredentials credentials = new BasicAWSCredentials(
                "AKIAVPWD6UOLSPEO7LVN",
                "DZX1YmjS4aVy9DEblbaOxWbD3tcFkOFbSa/tXrJC"
        );
        s3client = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.US_EAST_1)
                .build();
    }


    public void putFile(String key, File f){

        PutObjectResult result =
                s3client.putObject(new PutObjectRequest("toantestt", key, f).withCannedAcl(CannedAccessControlList.PublicRead));

        System.out.println(result);
//        s3client.putObject(
//                "toantestt",
//                "Document/pic.png",
//                new File("C:/Users/Admin/Desktop/pic.png")
//        );
//        List<Bucket> buckets = s3client.listBuckets();
//        for(Bucket bucket : buckets) {
//            System.out.println(bucket.getName());
//        }
//        ListObjectsV2Result res = space.listObjectsV2("toan");
//        List<S3ObjectSummary> objects = res.getObjectSummaries();
//        objects.stream().forEach(obj -> {
//            System.out.println(obj.toString());
//        });

    }

    public void deleteFile(String key) {
        s3client.deleteObject("toantestt",key);

    }



}

