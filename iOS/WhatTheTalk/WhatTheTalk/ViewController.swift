//
//  ViewController.swift
//  WhatTheTalk
//
//  Created by Louis Tsai on 2014/12/13.
//  Copyright (c) 2014年 Louis Tsai. All rights reserved.
//

import UIKit

class ViewController: UIViewController, NSStreamDelegate {
    var inputStream : NSInputStream?
    var outputStream : NSOutputStream?
    let port : UInt32 = 5566
    
    @IBOutlet weak var serverIP: UITextField!
    
    @IBOutlet weak var outMsg: UITextField!
  
    @IBOutlet weak var inMsg: UITextField!
    
    @IBAction func toConnectServer(sender: AnyObject) {
        if (serverIP.text == "") {
            serverIP.text = "IP cannot be empty!"
            return
        }
        self.initConnection()
    }
    @IBAction func toSendMsg(sender: AnyObject) {
        self.send()
    }
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // Do any additional setup after loading the view, typically from a nib.
        serverIP.text = "localhost"
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    func stream(aStream: NSStream, handleEvent eventCode: NSStreamEvent) {
        switch eventCode {
        case NSStreamEvent.HasBytesAvailable:
            if (aStream===inputStream) {
                var buf = UnsafeMutablePointer<UInt8>.alloc(1024)
                let len = inputStream!.read(buf, maxLength:1024)
                if (len > 0) {
                    let bufData = NSData(bytes : UnsafePointer<Void> (buf), length : len)
                    let str = NSString (data: bufData, encoding : NSASCIIStringEncoding)
                    inMsg!.text = str
                    println("Get incoming message : \(inMsg!.text)")
                }
            }
        default:
            break
        }
    }
    
    func send () {
        var str : String = outMsg.text!
        str += "\r\n"
        var buf : NSData = str.dataUsingEncoding(NSASCIIStringEncoding)!
        outputStream!.write(UnsafeMutablePointer<UInt8>(buf.bytes), maxLength:str.lengthOfBytesUsingEncoding(NSASCIIStringEncoding))
        println(str)
    }
    
    func initConnection() {
        var readStream : Unmanaged<CFReadStream>?
        var writeStream : Unmanaged<CFWriteStream>?
        var host : CFString = NSString (string : serverIP.text)
        //var host : CFString = NSString (string : "localhost")
        
        CFStreamCreatePairWithSocketToHost(kCFAllocatorDefault, host, port, &readStream, &writeStream)
        inputStream = readStream!.takeUnretainedValue()
        outputStream = writeStream!.takeUnretainedValue()
        
        inputStream!.delegate = self
        outputStream!.delegate = self
        
        inputStream!.scheduleInRunLoop(NSRunLoop.currentRunLoop(), forMode: NSDefaultRunLoopMode)
        outputStream!.scheduleInRunLoop(NSRunLoop.currentRunLoop(), forMode: NSDefaultRunLoopMode)
        
        inputStream!.open()
        outputStream!.open()
        
        
    }


}

