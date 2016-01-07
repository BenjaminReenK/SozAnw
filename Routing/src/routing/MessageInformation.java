/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package routing;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import net.sharkfw.knowledgeBase.Information;
import net.sharkfw.knowledgeBase.SharkKBException;

/**
 *
 * @author s0546862 / s0546935
 */
public class MessageInformation implements Information{

    private long creationTime;
    private long lastModified;
    private byte[] contentAsByte;
    private String contentAsString;
    private long contentLength;
    private String contentType;
    private String informationName;
    
    public MessageInformation() {
        this.creationTime = System.currentTimeMillis();
        this.lastModified = 0;
        this.contentLength = 0;
        this.contentType = "";
        this.informationName = "";
    }
    
    @Override
    public long lastModified() {
        return this.lastModified;
    }

    @Override
    public long creationTime() {
        return this.creationTime;
    }

    @Override
    public void setContent(InputStream is, long len) {
        // TODO Auto-generated method stub
    }

    @Override
    public void setContent(byte[] content) {
        this.contentAsByte = content;
        this.lastModified = System.currentTimeMillis();
        this.contentLength = content.length;
    }

    @Override
    public void setContent(String content) {
        this.contentAsString = content;
        this.lastModified = System.currentTimeMillis();
        this.contentLength = content.length();
    }

    @Override
    public void removeContent() {
        this.contentAsByte = null;
        this.contentAsString = "";
    }

    @Override
    public void setContentType(String mimetype) {
        this.contentType = mimetype;
    }

    @Override
    public String getContentType() {
        return this.contentType;
    }

    @Override
    public byte[] getContentAsByte() {
        return this.contentAsByte;
    }

    @Override
    public void streamContent(OutputStream os) {
        // TODO Auto-generated method stub
    }

    @Override
    public long getContentLength() {
        return this.contentLength;
    }

    @Override
    public OutputStream getOutputStream() throws SharkKBException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public InputStream getInputStream() throws SharkKBException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getName() {
        return this.informationName;
    }

    @Override
    public String getContentAsString() throws SharkKBException {
        return this.contentAsString;
    }

    @Override
    public void setName(String name) throws SharkKBException {
        this.informationName = name;
    }

    @Override
    public String getUniqueID() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setSystemProperty(String name, String value) {
        // TODO Auto-generated method stub
    }

    @Override
    public String getSystemProperty(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setProperty(String name, String value) throws SharkKBException {
        // TODO Auto-generated method stub
    }

    @Override
    public String getProperty(String name) throws SharkKBException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setProperty(String name, String value, boolean transfer) throws SharkKBException {
        // TODO Auto-generated method stub
    }

    @Override
    public void removeProperty(String name) throws SharkKBException {
        // TODO Auto-generated method stub
    }

    @Override
    public Enumeration<String> propertyNames() throws SharkKBException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Enumeration<String> propertyNames(boolean all) throws SharkKBException {
        // TODO Auto-generated method stub
        return null;
    }
    
}
