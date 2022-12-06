package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;
import edu.byu.cs.tweeter.server.dao.interfaces.S3DAOInterface;

public class S3DAO extends KingDAO implements S3DAOInterface {

    @Override
    public boolean addImage(String image, String username) {
        System.out.println("Trying to upload to S3 Bucket");
        try {
            //Convert the image and push it as a public image
            byte[] bytes = Base64.getDecoder().decode(image);
            InputStream target = new ByteArrayInputStream(bytes);
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(bytes.length);
            metadata.setContentType("image/png");
            gets3Client().putObject(new PutObjectRequest(s3BucketName, username, target, metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
            return true;

        } catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
    }
}
