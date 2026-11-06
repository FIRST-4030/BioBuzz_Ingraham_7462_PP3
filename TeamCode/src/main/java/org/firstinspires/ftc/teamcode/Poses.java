package org.firstinspires.ftc.teamcode;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.math.Pose;

import org.firstinspires.ftc.teamcode.pedro.procedures.ForesightTuner;

public class Poses {
    private final double FORWARD = 90;
    private final double ROBOT_SIZE = 16;
    //constructor and other important stuff
    private final PoseFactory poseFactory = PoseFactory.degrees();
    public Poses(){}

    //left side park
    public final Pose startPosLeft = poseFactory.of(62, 12, FORWARD);
    public final Pose shootPosLeft = poseFactory.of(58, 23, FORWARD);

    public final Pose collectControlPosLeft = poseFactory.of(35, 10 , FORWARD);
    public final Pose collectPosLeft = poseFactory.of(12, 12, FORWARD);

    public final Pose parkControlPosLeft = poseFactory.of(20, 49 , FORWARD);
    public final Pose parkPosLeft = poseFactory.of(13, 94, FORWARD);

    public final Pose preCollectPoseLeft = poseFactory.of(30, 12, FORWARD);

    //worst case
    public final Pose parkNoHeading = poseFactory.of(9, 94, FORWARD);
    public final Pose zero = poseFactory.of(0,0, FORWARD);

    //right side park
    public final Pose startPosRight = poseFactory.of(78, ROBOT_SIZE/2, FORWARD);
    public final Pose shootPosRight = poseFactory.of(82, 30, FORWARD);
    public final Pose preParkPosRight = poseFactory.of(144 - ROBOT_SIZE/2, ROBOT_SIZE/2, FORWARD);
    public final Pose parkPosRight = poseFactory.of(140 - ROBOT_SIZE/2, 30, FORWARD);




    //Useful methods

    public Pose of (double x, double y, double heading) {
        return poseFactory.of(x, y, heading);
    }

    public Pose of (double x, double y) {
        return poseFactory.of(x, y, FORWARD);
    }

}
